package com.siec_acc.service.serviceImpl;

import com.siec_acc.dto.request.SalesOrderItemRequestDto;
import com.siec_acc.dto.request.SalesOrderRequestDto;
import com.siec_acc.dto.response.SalesOrderItemResponseDto;
import com.siec_acc.dto.response.SalesOrderResponseDto;
import com.siec_acc.entity.ClientEntity;
import com.siec_acc.entity.ItemEntity;
import com.siec_acc.entity.SalesOrderEntity;
import com.siec_acc.entity.SalesOrderItemEntity;
import com.siec_acc.exceptions.InvalidOperationException;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.repository.ClientRepository;
import com.siec_acc.repository.ItemRepository;
import com.siec_acc.repository.SalesOrderRepository;
import com.siec_acc.service.SalesOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

    // Approval status (what the user decides)
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";
    private static final Set<String> ALLOWED_STATUSES = Set.of(STATUS_PENDING, STATUS_APPROVED, STATUS_REJECTED);

    // Fulfilment status (driven by delivery challans / invoices - never set by the client)
    private static final String FULFIL_PENDING = "pending";
    private static final String FULFIL_PARTIAL = "partial";
    private static final String FULFIL_DONE = "fulfilled";

    private static final String ID_PREFIX = "SO-";

    private final SalesOrderRepository salesOrderRepository;
    private final ClientRepository customerRepository;
    private final ItemRepository itemRepository;
    private final TransactionTemplate transactionTemplate;

    /**
     * Customers / items have no API or seed data yet, so by default we only check that an id was sent.
     * Once those tables are populated, set  salesorder.validate-references=true  in application.properties
     * to also reject unknown customer / item ids.
     */
    @Value("${salesorder.validate-references:false}")
    private boolean validateReferences;

    /** Serialises number generation so two requests cannot be handed the same SO number (single instance). */
    private final Object numberLock = new Object();

    public SalesOrderServiceImpl(SalesOrderRepository salesOrderRepository,
                                 ClientRepository customerRepository,
                                 ItemRepository itemRepository,
                                 TransactionTemplate transactionTemplate) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.transactionTemplate = transactionTemplate;
    }

    // ------------------------------------------------------------------
    // internal value holders
    // ------------------------------------------------------------------
    private record ItemLine(String itemId, int qty, double rate) {}

    private record Header(String customerId, LocalDate date, LocalDate expectedDelivery, String deliveryLocation,
                          String paymentTerms, double discount, double tax, String notes,
                          String quotationReference, String piReference) {}

    // ------------------------------------------------------------------
    // CREATE - always starts as pending / pending; the server owns id, number, status and totals.
    // The lock is held across the whole transaction (incl. commit) so the next request sees this order.
    // ------------------------------------------------------------------
    @Override
    public SalesOrderResponseDto createSalesOrder(SalesOrderRequestDto dto) {
        if (dto == null) throw new InvalidOperationException("Request body is required.");

        Header header = buildHeader(
                dto.getCustomerId(),
                parseDate(dto.getDate(), "Order date"),
                parseDate(dto.getExpectedDelivery(), "Expected delivery date"),
                dto.getDeliveryLocation(), dto.getPaymentTerms(),
                dto.getDiscount(), dto.getTax(), dto.getNotes(),
                dto.getQuotationReference(), dto.getPiReference());
        List<ItemLine> lines = validateItems(dto.getItems());

        synchronized (numberLock) {
            SalesOrderResponseDto response = transactionTemplate.execute(status -> {
                SalesOrderEntity entity = new SalesOrderEntity();
                assignIdentity(entity, header.date());
                applyHeader(entity, header);
                entity.setStatus(STATUS_PENDING);
                entity.setFulfillmentStatus(FULFIL_PENDING);
                replaceItemsAndTotals(entity, lines);
                SalesOrderEntity saved = salesOrderRepository.saveAndFlush(entity);
                logger.info("Sales order {} ({}) created", saved.getNumber(), saved.getId());
                return toResponse(saved); // mapped inside the transaction
            });
            return response;
        }
    }

    // ------------------------------------------------------------------
    // READS
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public SalesOrderResponseDto getSalesOrderById(String id) {
        return toResponse(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrderResponseDto> getAllSalesOrders() {
        return toResponses(salesOrderRepository.findAll());
    }

    // ------------------------------------------------------------------
    // UPDATE (PUT) - full replace of the editable content. Status / fulfilment / number are untouched:
    // status changes go through PATCH (approve / reject), fulfilment is system-driven.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public SalesOrderResponseDto updateSalesOrder(String id, SalesOrderRequestDto dto) {
        if (dto == null) throw new InvalidOperationException("Request body is required.");
        SalesOrderEntity entity = getOrThrow(id);
        assertEditable(entity);

        Header header = buildHeader(
                dto.getCustomerId(),
                parseDate(dto.getDate(), "Order date"),
                parseDate(dto.getExpectedDelivery(), "Expected delivery date"),
                dto.getDeliveryLocation(), dto.getPaymentTerms(),
                dto.getDiscount(), dto.getTax(), dto.getNotes(),
                dto.getQuotationReference(), dto.getPiReference());
        List<ItemLine> lines = validateItems(dto.getItems());

        applyHeader(entity, header);
        replaceItemsAndTotals(entity, lines);

        SalesOrderEntity saved = salesOrderRepository.save(entity);
        logger.info("Sales order {} updated", saved.getNumber());
        return toResponse(saved);
    }

    // ------------------------------------------------------------------
    // PATCH - only non-null fields are applied. A status-only patch is the approve / reject action.
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public SalesOrderResponseDto patchSalesOrder(String id, SalesOrderRequestDto dto) {
        if (dto == null) throw new InvalidOperationException("Request body is required.");
        SalesOrderEntity entity = getOrThrow(id);

        boolean contentChange = dto.getCustomerId() != null || dto.getDate() != null
                || dto.getExpectedDelivery() != null || dto.getDeliveryLocation() != null
                || dto.getPaymentTerms() != null || dto.getDiscount() != null || dto.getTax() != null
                || dto.getNotes() != null || dto.getQuotationReference() != null
                || dto.getPiReference() != null || dto.getItems() != null;

        if (contentChange) {
            assertEditable(entity);

            // merge: anything not sent keeps its stored value, then the merged result is validated as a whole
            Header header = buildHeader(
                    dto.getCustomerId() != null ? dto.getCustomerId() : entity.getCustomerId(),
                    dto.getDate() != null ? parseDate(dto.getDate(), "Order date") : entity.getDate(),
                    dto.getExpectedDelivery() != null ? parseDate(dto.getExpectedDelivery(), "Expected delivery date") : entity.getExpectedDelivery(),
                    dto.getDeliveryLocation() != null ? dto.getDeliveryLocation() : entity.getDeliveryLocation(),
                    dto.getPaymentTerms() != null ? dto.getPaymentTerms() : entity.getPaymentTerms(),
                    dto.getDiscount() != null ? dto.getDiscount() : entity.getDiscount(),
                    dto.getTax() != null ? dto.getTax() : entity.getTax(),
                    dto.getNotes() != null ? dto.getNotes() : entity.getNotes(),
                    dto.getQuotationReference() != null ? dto.getQuotationReference() : entity.getQuotationReference(),
                    dto.getPiReference() != null ? dto.getPiReference() : entity.getPiReference());
            applyHeader(entity, header);

            if (dto.getItems() != null) {
                replaceItemsAndTotals(entity, validateItems(dto.getItems()));
            } else {
                // discount / tax may have changed - recompute off the stored items
                List<ItemLine> existing = entity.getItems().stream()
                        .map(i -> new ItemLine(i.getItemId(), i.getQty(), i.getRate()))
                        .collect(Collectors.toList());
                applyTotals(entity, existing);
            }
        }

        if (dto.getStatus() != null) {
            applyStatusTransition(entity, dto.getStatus());
        }

        SalesOrderEntity saved = salesOrderRepository.save(entity);
        logger.info("Sales order {} patched", saved.getNumber());
        return toResponse(saved);
    }

    // ------------------------------------------------------------------
    // DELETE - not once delivery has started (that would orphan the delivery / invoice trail)
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public void deleteSalesOrder(String id) {
        SalesOrderEntity entity = getOrThrow(id);
        String fulfilment = lc(entity.getFulfillmentStatus());
        if (FULFIL_PARTIAL.equals(fulfilment) || FULFIL_DONE.equals(fulfilment)) {
            throw new InvalidOperationException("Cannot delete " + entity.getNumber()
                    + ": delivery against it has already started.");
        }
        salesOrderRepository.delete(entity);
        logger.info("Sales order {} deleted", entity.getNumber());
    }

    // ==================================================================
    // rules
    // ==================================================================

    /** Rejected orders are closed; once delivery has started the content is frozen. */
    private void assertEditable(SalesOrderEntity entity) {
        String status = lc(entity.getStatus());
        if (STATUS_REJECTED.equals(status)) {
            throw new InvalidOperationException(entity.getNumber() + " is rejected and can no longer be edited.");
        }
        String fulfilment = lc(entity.getFulfillmentStatus());
        if (FULFIL_PARTIAL.equals(fulfilment) || FULFIL_DONE.equals(fulfilment) || FULFIL_DONE.equals(status)) {
            throw new InvalidOperationException(entity.getNumber()
                    + " can no longer be edited: delivery against it has already started.");
        }
    }

    /** pending -> approved | rejected. Anything else is refused. */
    private void applyStatusTransition(SalesOrderEntity entity, String rawTarget) {
        String target = lc(rawTarget);
        if (!ALLOWED_STATUSES.contains(target)) {
            throw new InvalidOperationException("Invalid status '" + rawTarget + "'. Allowed: pending, approved, rejected.");
        }
        String current = isBlank(entity.getStatus()) ? STATUS_PENDING : lc(entity.getStatus());
        if (target.equals(current)) return;
        if (!STATUS_PENDING.equals(current)) {
            throw new InvalidOperationException("Only pending orders can be " + target + ". "
                    + entity.getNumber() + " is currently " + current + ".");
        }
        entity.setStatus(target);
    }

    // ==================================================================
    // validation
    // ==================================================================
    private Header buildHeader(String customerId, LocalDate date, LocalDate expectedDelivery, String deliveryLocation,
                               String paymentTerms, Double discount, Double tax, String notes,
                               String quotationReference, String piReference) {
        if (isBlank(customerId)) throw new InvalidOperationException("Customer is required.");
        if (date == null) throw new InvalidOperationException("Order date is required.");
        if (expectedDelivery == null) throw new InvalidOperationException("Expected delivery date is required.");
        if (expectedDelivery.isBefore(date)) {
            throw new InvalidOperationException("Expected delivery date cannot be before the order date.");
        }
        double disc = discount == null ? 0.0 : discount;
        double taxPct = tax == null ? 0.0 : tax;
        if (!isPercent(disc)) throw new InvalidOperationException("Discount must be between 0 and 100.");
        if (!isPercent(taxPct)) throw new InvalidOperationException("Tax must be between 0 and 100.");

        String cid = customerId.trim();
        if (validateReferences && !customerRepository.existsById(cid)) {
            throw new InvalidOperationException("Customer '" + cid + "' does not exist.");
        }
        return new Header(cid, date, expectedDelivery, trimToNull(deliveryLocation), trimToNull(paymentTerms),
                disc, taxPct, trimToNull(notes), trimToNull(quotationReference), trimToNull(piReference));
    }

    private List<ItemLine> validateItems(List<SalesOrderItemRequestDto> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOperationException("Add at least one item to the sales order.");
        }
        List<ItemLine> lines = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (SalesOrderItemRequestDto it : items) {
            if (it == null || isBlank(it.getItemId())) {
                throw new InvalidOperationException("Every line item needs an item.");
            }
            String itemId = it.getItemId().trim();
            if (it.getQty() == null || it.getQty() <= 0) {
                throw new InvalidOperationException("Quantity must be greater than 0 for item '" + itemId + "'.");
            }
            if (it.getRate() == null || it.getRate().isNaN() || it.getRate().isInfinite() || it.getRate() < 0) {
                throw new InvalidOperationException("Rate cannot be negative for item '" + itemId + "'.");
            }
            if (!seen.add(itemId)) {
                throw new InvalidOperationException("Item '" + itemId + "' appears more than once - merge the lines.");
            }
            if (validateReferences && !itemRepository.existsById(itemId)) {
                throw new InvalidOperationException("Item '" + itemId + "' does not exist.");
            }
            lines.add(new ItemLine(itemId, it.getQty(), it.getRate()));
        }
        return lines;
    }

    private LocalDate parseDate(String raw, String label) {
        if (isBlank(raw)) throw new InvalidOperationException(label + " is required.");
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException ex) {
            throw new InvalidOperationException(label + " must be a valid date (yyyy-MM-dd).");
        }
    }

    // ==================================================================
    // building the entity
    // ==================================================================
    private void applyHeader(SalesOrderEntity e, Header h) {
        e.setCustomerId(h.customerId());
        e.setDate(h.date());
        e.setExpectedDelivery(h.expectedDelivery());
        e.setDeliveryLocation(h.deliveryLocation());
        e.setPaymentTerms(h.paymentTerms());
        e.setDiscount(h.discount());
        e.setTax(h.tax());
        e.setNotes(h.notes());
        e.setQuotationReference(h.quotationReference());
        e.setPiReference(h.piReference());
    }

    private void replaceItemsAndTotals(SalesOrderEntity entity, List<ItemLine> lines) {
        entity.getItems().clear();
        for (ItemLine l : lines) {
            SalesOrderItemEntity row = new SalesOrderItemEntity();
            row.setItemId(l.itemId());
            row.setQty(l.qty());
            row.setRate(l.rate());
            row.setSalesOrder(entity);
            entity.getItems().add(row);
        }
        applyTotals(entity, lines);
    }

    private void applyTotals(SalesOrderEntity entity, List<ItemLine> lines) {
        double subtotal = 0.0;
        for (ItemLine l : lines) subtotal += l.qty() * l.rate();
        subtotal = round2(subtotal);

        double discountAmt = round2(subtotal * entity.getDiscount() / 100.0);
        double taxable = subtotal - discountAmt;
        double taxAmt = round2(taxable * entity.getTax() / 100.0);

        entity.setSubtotal(subtotal);
        entity.setTotal(round2(taxable + taxAmt));
    }

    /**
     * id     -> SO-001, SO-002 ... (global, one more than the highest in use)
     * number -> SO/26-27/001 ... (restarts every Indian financial year, Apr-Mar, taken from the order date)
     * Never uses count(): after a delete count()+1 would hand out a number that already exists, and because
     * the id is assigned by hand save() would then silently overwrite that existing order.
     */
    private void assignIdentity(SalesOrderEntity entity, LocalDate orderDate) {
        int maxId = 0;
        for (String id : salesOrderRepository.findAllIds()) {
            maxId = Math.max(maxId, trailingNumber(id, ID_PREFIX));
        }
        int nextId = maxId + 1;
        while (salesOrderRepository.existsById(ID_PREFIX + pad(nextId))) nextId++;

        String prefix = "SO/" + financialYear(orderDate) + "/";
        int maxNo = 0;
        for (String number : salesOrderRepository.findNumbersByPrefix(prefix)) {
            maxNo = Math.max(maxNo, trailingNumber(number, prefix));
        }

        entity.setId(ID_PREFIX + pad(nextId));
        entity.setNumber(prefix + pad(maxNo + 1));
    }

    private static String financialYear(LocalDate d) {
        int startYear = d.getMonthValue() >= 4 ? d.getYear() : d.getYear() - 1;
        return String.format("%02d-%02d", startYear % 100, (startYear + 1) % 100);
    }

    private static int trailingNumber(String value, String prefix) {
        if (value == null || !value.startsWith(prefix)) return 0;
        try {
            return Integer.parseInt(value.substring(prefix.length()));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static String pad(int n) { return String.format("%03d", n); }

    // ==================================================================
    // response mapping - customer / item names are loaded in two batched queries, not one per row
    // ==================================================================
    private SalesOrderEntity getOrThrow(String id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No sales order found with ID '" + id + "'."));
    }

    private SalesOrderResponseDto toResponse(SalesOrderEntity entity) {
        return toResponses(List.of(entity)).get(0);
    }

    private List<SalesOrderResponseDto> toResponses(List<SalesOrderEntity> entities) {
        Set<String> customerIds = new HashSet<>();
        Set<String> itemIds = new HashSet<>();
        for (SalesOrderEntity e : entities) {
            if (e.getCustomerId() != null) customerIds.add(e.getCustomerId());
            for (SalesOrderItemEntity i : e.getItems()) {
                if (i.getItemId() != null) itemIds.add(i.getItemId());
            }
        }
        Map<String, String> customerNames = new HashMap<>();
        for (ClientEntity c : customerRepository.findAllById(customerIds)) customerNames.put(c.getId(), c.getName());
        Map<String, String> itemNames = new HashMap<>();
        for (ItemEntity it : itemRepository.findAllById(itemIds)) itemNames.put(it.getId(), it.getName());

        List<SalesOrderResponseDto> out = new ArrayList<>();
        for (SalesOrderEntity e : entities) out.add(mapToResponseDto(e, customerNames, itemNames));
        return out;
    }

    private SalesOrderResponseDto mapToResponseDto(SalesOrderEntity e, Map<String, String> customerNames,
                                                   Map<String, String> itemNames) {
        SalesOrderResponseDto dto = new SalesOrderResponseDto();
        dto.setId(e.getId());
        dto.setNumber(e.getNumber());
        dto.setCustomerId(e.getCustomerId());
        dto.setCustomerName(customerNames.get(e.getCustomerId()));
        dto.setDate(e.getDate() == null ? null : e.getDate().toString());
        dto.setExpectedDelivery(e.getExpectedDelivery() == null ? null : e.getExpectedDelivery().toString());
        dto.setDeliveryLocation(e.getDeliveryLocation());
        dto.setPaymentTerms(e.getPaymentTerms());
        dto.setStatus(e.getStatus());
        dto.setFulfillmentStatus(e.getFulfillmentStatus());
        dto.setDiscount(e.getDiscount());
        dto.setTax(e.getTax());
        dto.setSubtotal(e.getSubtotal());
        dto.setTotal(e.getTotal());
        dto.setNotes(e.getNotes());
        dto.setQuotationReference(e.getQuotationReference());
        dto.setPiReference(e.getPiReference());

        List<SalesOrderItemResponseDto> itemDtos = new ArrayList<>();
        for (SalesOrderItemEntity row : e.getItems()) {
            SalesOrderItemResponseDto itemDto = new SalesOrderItemResponseDto();
            itemDto.setItemId(row.getItemId());
            itemDto.setName(itemNames.get(row.getItemId()));
            itemDto.setQty(row.getQty());
            itemDto.setRate(row.getRate());
            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);
        return dto;
    }

    // ==================================================================
    // small helpers
    // ==================================================================
    private static boolean isPercent(double v) { return !Double.isNaN(v) && v >= 0.0 && v <= 100.0; }

    private static double round2(double n) { return Math.round(n * 100.0) / 100.0; }

    private static String lc(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static String trimToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}