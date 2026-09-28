package com.siec_acc.service.serviceImpl;

import com.siec_acc.dto.request.ClientRequestDto;
import com.siec_acc.dto.response.ClientResponseDto;
import com.siec_acc.entity.ClientEntity;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.repository.ClientRepository;
import com.siec_acc.service.ClientService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final List<String> COLOR_PALETTE = Arrays.asList(
            "#6da84c", "#1985bf", "#d97706", "#7c3aed", "#dc2626",
            "#0891b2", "#4f46e5", "#e11d48", "#059669", "#ca8a04"
    );

    private static final List<String> PAYMENT_TERMS_OPTIONS = Arrays.asList(
            "Net 7 days", "Net 15 days", "Net 30 days", "Net 45 days",
            "Net 60 days", "Advance Payment", "Cash on Delivery"
    );

    private static final List<String> CUSTOMER_TYPE_OPTIONS = Arrays.asList("business", "individual", "government");

    private static final List<String> STATUS_OPTIONS = Arrays.asList("active", "inactive");

    private static final Map<String, String> STATE_CODE_MAP = buildStateCodeMap();

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public ClientResponseDto createClient(ClientRequestDto requestDto) {
        logger.info("Creating new client with name: {}", requestDto.getName());

        ClientEntity client = new ClientEntity();
        client.setClientName(requestDto.getName());
        client.setClientCompanyName(requestDto.getCompanyName() != null ? requestDto.getCompanyName() : "");
        client.setClientCustomerType(requestDto.getCustomerType() != null ? requestDto.getCustomerType() : "business");
        client.setClientGstin(requestDto.getGstin() != null ? requestDto.getGstin() : "");
        client.setClientPan(requestDto.getPan());
        client.setClientEmail(requestDto.getEmail());
        client.setClientPhone(requestDto.getPhone());
        client.setClientBillingAddress(requestDto.getBillingAddress());
        client.setClientShippingAddress(requestDto.getShippingAddress());
        client.setClientCity(requestDto.getCity());
        client.setClientState(requestDto.getState());
        client.setClientStateCode(resolveStateCode(requestDto.getState(), requestDto.getStateCode()));
        client.setClientPincode(requestDto.getPincode());
        client.setClientPaymentTerms(requestDto.getPaymentTerms());
        client.setClientCreditLimit(requestDto.getCreditLimit() != null ? requestDto.getCreditLimit() : BigDecimal.ZERO);
        client.setClientGstRegistered(requestDto.getGstRegistered() != null ? requestDto.getGstRegistered() : Boolean.FALSE);
        client.setClientStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "active");
        client.setClientReceivable(requestDto.getReceivable() != null ? requestDto.getReceivable() : BigDecimal.ZERO);
        client.setClientCreatedAt(LocalDate.now());

        ClientEntity savedClient = clientRepository.save(client);
        savedClient.setClientStrId(generateCustomerId(savedClient.getClientPrimeId()));
        savedClient.setClientColor(requestDto.getColor() != null && !requestDto.getColor().isBlank()
                ? requestDto.getColor()
                : pickColor(savedClient.getClientPrimeId()));
        savedClient = clientRepository.save(savedClient);

        logger.info("Customer created successfully: {}", savedClient.getClientStrId());
        return mapToResponse(savedClient);
    }

    @Override
    @Transactional
    public ClientResponseDto updateClient(String id, ClientRequestDto requestDto) {
        logger.info("Updating customer: {}", id);
        ClientEntity client = getClientEntityOrThrow(id);

        if (requestDto.getName() != null) client.setClientName(requestDto.getName());
        if (requestDto.getCompanyName() != null) client.setClientCompanyName(requestDto.getCompanyName());
        if (requestDto.getCustomerType() != null) client.setClientCustomerType(requestDto.getCustomerType());
        if (requestDto.getGstin() != null) client.setClientGstin(requestDto.getGstin());
        if (requestDto.getPan() != null) client.setClientPan(requestDto.getPan());
        if (requestDto.getEmail() != null) client.setClientEmail(requestDto.getEmail());
        if (requestDto.getPhone() != null) client.setClientPhone(requestDto.getPhone());
        if (requestDto.getBillingAddress() != null) client.setClientBillingAddress(requestDto.getBillingAddress());
        if (requestDto.getShippingAddress() != null) client.setClientShippingAddress(requestDto.getShippingAddress());
        if (requestDto.getCity() != null) client.setClientCity(requestDto.getCity());
        if (requestDto.getState() != null) {
            client.setClientState(requestDto.getState());
            client.setClientStateCode(resolveStateCode(requestDto.getState(), requestDto.getStateCode()));
        } else if (requestDto.getStateCode() != null) {
            client.setClientStateCode(requestDto.getStateCode());
        }
        if (requestDto.getPincode() != null) client.setClientPincode(requestDto.getPincode());
        if (requestDto.getPaymentTerms() != null) client.setClientPaymentTerms(requestDto.getPaymentTerms());
        if (requestDto.getCreditLimit() != null) client.setClientCreditLimit(requestDto.getCreditLimit());
        if (requestDto.getGstRegistered() != null) client.setClientGstRegistered(requestDto.getGstRegistered());
        if (requestDto.getStatus() != null) client.setClientStatus(requestDto.getStatus());
        if (requestDto.getReceivable() != null) client.setClientReceivable(requestDto.getReceivable());
        if (requestDto.getColor() != null && !requestDto.getColor().isBlank()) client.setClientColor(requestDto.getColor());

        ClientEntity updated = clientRepository.save(client);

        logger.info("Customer updated successfully: {}", id);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public ClientResponseDto patchClient(String id, ClientRequestDto requestDto) {
        logger.info("Patching customer: {}", id);
        ClientEntity client = getClientEntityOrThrow(id);

        if (requestDto.getName() != null) client.setClientName(requestDto.getName());
        if (requestDto.getCompanyName() != null) client.setClientCompanyName(requestDto.getCompanyName());
        if (requestDto.getCustomerType() != null) client.setClientCustomerType(requestDto.getCustomerType());
        if (requestDto.getGstin() != null) client.setClientGstin(requestDto.getGstin());
        if (requestDto.getPan() != null) client.setClientPan(requestDto.getPan());
        if (requestDto.getEmail() != null) client.setClientEmail(requestDto.getEmail());
        if (requestDto.getPhone() != null) client.setClientPhone(requestDto.getPhone());
        if (requestDto.getBillingAddress() != null) client.setClientBillingAddress(requestDto.getBillingAddress());
        if (requestDto.getShippingAddress() != null) client.setClientShippingAddress(requestDto.getShippingAddress());
        if (requestDto.getCity() != null) client.setClientCity(requestDto.getCity());
        if (requestDto.getState() != null) {
            client.setClientState(requestDto.getState());
            client.setClientStateCode(resolveStateCode(requestDto.getState(), requestDto.getStateCode()));
        } else if (requestDto.getStateCode() != null) {
            client.setClientStateCode(requestDto.getStateCode());
        }
        if (requestDto.getPincode() != null) client.setClientPincode(requestDto.getPincode());
        if (requestDto.getPaymentTerms() != null) client.setClientPaymentTerms(requestDto.getPaymentTerms());
        if (requestDto.getCreditLimit() != null) client.setClientCreditLimit(requestDto.getCreditLimit());
        if (requestDto.getGstRegistered() != null) client.setClientGstRegistered(requestDto.getGstRegistered());
        if (requestDto.getStatus() != null) client.setClientStatus(requestDto.getStatus());
        if (requestDto.getReceivable() != null) client.setClientReceivable(requestDto.getReceivable());
        if (requestDto.getColor() != null && !requestDto.getColor().isBlank()) client.setClientColor(requestDto.getColor());

        ClientEntity patched = clientRepository.save(client);

        logger.info("Customer patched successfully: {}", id);
        return mapToResponse(patched);
    }

    @Override
    @Transactional
    public void deleteClient(String id) {
        logger.info("Deleting customer: {}", id);
        ClientEntity client = getClientEntityOrThrow(id);
        clientRepository.delete(client);
        logger.info("Customer deleted successfully: {}", id);
    }

    @Override
    public ClientResponseDto getClientById(String id) {
        logger.info("Fetching customer: {}", id);
        return mapToResponse(getClientEntityOrThrow(id));
    }

    // ---------- DB-level filtering + pagination ----------

    @Override
    public Map<String, Object> getAllClients(String status, String gst, String type, String search,
                                             Integer page, Integer size) {
        logger.info("Fetching customers | status={} gst={} type={} search={} page={} size={}",
                status, gst, type, search, page, size);

        String statusFilter = normalizeFilter(status);
        String gstFilter = normalizeFilter(gst);
        String typeFilter = normalizeFilter(type);
        String searchFilter = search != null ? search.trim() : "";

        Specification<ClientEntity> spec = buildSpecification(statusFilter, gstFilter, typeFilter, searchFilter);

        boolean paged = page != null || size != null;
        List<ClientResponseDto> customers;
        int pageNo;
        int pageSize;
        long totalItems;
        int totalPages;

        if (paged) {
            pageNo = (page == null || page < 1) ? 1 : page;
            pageSize = (size == null || size < 1) ? DEFAULT_PAGE_SIZE : size;
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.ASC, "clientPrimeId"));

            Page<ClientEntity> resultPage = clientRepository.findAll(spec, pageable);
            customers = resultPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());
            totalItems = resultPage.getTotalElements();
            totalPages = Math.max(1, resultPage.getTotalPages());
        } else {
            List<ClientEntity> filtered = clientRepository.findAll(spec);
            customers = filtered.stream().map(this::mapToResponse).collect(Collectors.toList());
            totalItems = filtered.size();
            pageNo = 1;
            pageSize = Math.max((int) totalItems, 1);
            totalPages = 1;
        }

        Map<String, Object> pagination = new LinkedHashMap<>();
        pagination.put("paged", paged);
        pagination.put("page", pageNo);
        pagination.put("size", pageSize);
        pagination.put("totalItems", totalItems);
        pagination.put("totalPages", totalPages);
        pagination.put("hasNext", pageNo < totalPages);
        pagination.put("hasPrevious", pageNo > 1);

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("status", statusFilter);
        filters.put("gst", gstFilter);
        filters.put("type", typeFilter);
        filters.put("search", searchFilter);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("customers", customers);
        response.put("pagination", pagination);
        response.put("meta", buildMeta(filters));
        return response;
    }

    private Specification<ClientEntity> buildSpecification(String statusFilter, String gstFilter,
                                                           String typeFilter, String searchFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!"all".equals(statusFilter)) {
                predicates.add(cb.equal(cb.lower(root.get("clientStatus")), statusFilter));
            }
            if (!"all".equals(typeFilter)) {
                predicates.add(cb.equal(cb.lower(root.get("clientCustomerType")), typeFilter));
            }
            if ("registered".equals(gstFilter)) {
                predicates.add(cb.isTrue(root.get("clientGstRegistered")));
            } else if ("unregistered".equals(gstFilter)) {
                predicates.add(cb.or(cb.isFalse(root.get("clientGstRegistered")), cb.isNull(root.get("clientGstRegistered"))));
            }
            if (!searchFilter.isBlank()) {
                String like = "%" + searchFilter.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("clientStrId")), like),
                        cb.like(cb.lower(root.get("clientName")), like),
                        cb.like(cb.lower(root.get("clientCompanyName")), like),
                        cb.like(cb.lower(root.get("clientEmail")), like),
                        cb.like(cb.lower(root.get("clientPhone")), like),
                        cb.like(cb.lower(root.get("clientGstin")), like),
                        cb.like(cb.lower(root.get("clientCity")), like)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Map<String, Object> getMeta() {
        logger.info("Fetching customer meta");

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("status", "all");
        filters.put("gst", "all");
        filters.put("type", "all");
        filters.put("search", "");

        return buildMeta(filters);
    }

    // ---------- helpers ----------

    private ClientEntity getClientEntityOrThrow(String id) {
        return clientRepository.findByClientStrId(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found: {}", id);
                    return new ResourceNotFoundException("No customer found with ID '" + id + "'.");
                });
    }

    private String generateCustomerId(Long primeId) {
        return String.format("CUS-%03d", primeId);
    }

    private String pickColor(Long primeId) {
        int index = (int) ((primeId - 1) % COLOR_PALETTE.size());
        return COLOR_PALETTE.get(index);
    }

    private String resolveStateCode(String state, String stateCode) {
        if (stateCode != null && !stateCode.isBlank()) return stateCode;
        if (state != null && STATE_CODE_MAP.containsKey(state)) return STATE_CODE_MAP.get(state);
        return stateCode;
    }

    private String normalizeFilter(String value) {
        return (value == null || value.isBlank()) ? "all" : value.trim().toLowerCase();
    }

    // Aggregate stats (cards + dropdown options) are always computed over the WHOLE table,
    // regardless of the current filters — that part still needs a full read.
    private Map<String, Object> buildMeta(Map<String, Object> filters) {
        List<ClientEntity> allClients = clientRepository.findAll();

        long nextCustomerNo = allClients.stream()
                .map(ClientEntity::getClientPrimeId)
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;

        long activeCustomers = allClients.stream()
                .filter(c -> "active".equalsIgnoreCase(c.getClientStatus()))
                .count();

        long gstRegisteredCustomers = allClients.stream()
                .filter(c -> Boolean.TRUE.equals(c.getClientGstRegistered()))
                .count();

        BigDecimal totalReceivable = allClients.stream()
                .map(c -> c.getClientReceivable() != null ? c.getClientReceivable() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("nextCustomerNo", nextCustomerNo);
        meta.put("totalCustomers", allClients.size());
        meta.put("activeCustomers", activeCustomers);
        meta.put("gstRegisteredCustomers", gstRegisteredCustomers);
        meta.put("totalReceivable", totalReceivable);
        meta.put("filters", filters);
        meta.put("stateCodeMap", STATE_CODE_MAP);
        meta.put("paymentTermsOptions", PAYMENT_TERMS_OPTIONS);
        meta.put("customerTypeOptions", CUSTOMER_TYPE_OPTIONS);
        meta.put("statusOptions", STATUS_OPTIONS);
        return meta;
    }

    private static Map<String, String> buildStateCodeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Maharashtra", "27");
        map.put("Delhi", "07");
        map.put("Karnataka", "29");
        map.put("Tamil Nadu", "33");
        map.put("Gujarat", "24");
        map.put("Uttar Pradesh", "09");
        map.put("Rajasthan", "08");
        map.put("Kerala", "32");
        map.put("Telangana", "36");
        map.put("West Bengal", "19");
        map.put("Andhra Pradesh", "28");
        map.put("Bihar", "10");
        map.put("Chhattisgarh", "22");
        map.put("Goa", "30");
        map.put("Haryana", "06");
        map.put("Himachal Pradesh", "02");
        map.put("Jharkhand", "20");
        map.put("Madhya Pradesh", "23");
        map.put("Odisha", "21");
        map.put("Punjab", "03");
        map.put("Uttarakhand", "05");
        return map;
    }

    private ClientResponseDto mapToResponse(ClientEntity client) {
        ClientResponseDto response = new ClientResponseDto();
        response.setId(client.getClientStrId());
        response.setName(client.getClientName());
        response.setCompanyName(client.getClientCompanyName());
        response.setCustomerType(client.getClientCustomerType());
        response.setGstin(client.getClientGstin());
        response.setPan(client.getClientPan());
        response.setEmail(client.getClientEmail());
        response.setPhone(client.getClientPhone());
        response.setBillingAddress(client.getClientBillingAddress());
        response.setShippingAddress(client.getClientShippingAddress());
        response.setCity(client.getClientCity());
        response.setState(client.getClientState());
        response.setStateCode(client.getClientStateCode());
        response.setPincode(client.getClientPincode());
        response.setPaymentTerms(client.getClientPaymentTerms());
        response.setCreditLimit(client.getClientCreditLimit());
        response.setGstRegistered(client.getClientGstRegistered());
        response.setStatus(client.getClientStatus());
        response.setReceivable(client.getClientReceivable());
        response.setCreatedAt(client.getClientCreatedAt() != null
                ? client.getClientCreatedAt().format(DATE_FORMATTER) : null);
        response.setColor(client.getClientColor());
        return response;
    }
}