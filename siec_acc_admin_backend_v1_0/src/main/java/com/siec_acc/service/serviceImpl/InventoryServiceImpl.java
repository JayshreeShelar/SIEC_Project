package com.siec_acc.service.serviceImpl;

import com.siec_acc.entity.InventoryHistoryEntity;
import com.siec_acc.entity.ProductEntity;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.utils.StrIdGenerator;
import com.siec_acc.dto.response.InventoryResponseDTO;
import com.siec_acc.dto.request.InventoryStockUpdateDTO;
import com.siec_acc.entity.InventoryEntity;
import com.siec_acc.repository.InventoryHistoryRepository;
import com.siec_acc.repository.InventoryRepository;
import com.siec_acc.service.InventoryService;

import com.siec_acc.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;

    @Override
    public InventoryResponseDTO getInventoryByProductStrId(String productStrId) {
        logger.info("Fetching inventory for productStrId: {}", productStrId);
        return mapToResponse(getInventoryOrThrow(productStrId));
    }

    @Override
    @Transactional
    public InventoryResponseDTO addStock(String productStrId, InventoryStockUpdateDTO requestDTO) {
        logger.info("Adding stock | productStrId={} | qty={}", productStrId, requestDTO.getChangeQty());

        if (requestDTO.getChangeQty() == null || requestDTO.getChangeQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Stock quantity to add must be greater than zero.");
        }

        InventoryEntity inventory = getInventoryOrThrow(productStrId);
        BigDecimal previousStock = inventory.getProductStock() != null ? inventory.getProductStock() : BigDecimal.ZERO;
        BigDecimal newStock = previousStock.add(requestDTO.getChangeQty());

        inventory.setProductStock(newStock);
        InventoryEntity saved = inventoryRepository.save(inventory);

        updateProductStatus(inventory.getProduct(), newStock);
        logHistory(inventory.getProduct(), "STOCK_ADD", previousStock, newStock, requestDTO.getChangeQty(),
                requestDTO.getRemarks() != null ? requestDTO.getRemarks() : "Stock added");

        logger.info("Stock added successfully | productStrId={} | newStock={}", productStrId, newStock);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponseDTO reduceStock(String productStrId, InventoryStockUpdateDTO requestDTO) {
        logger.info("Reducing stock | productStrId={} | qty={}", productStrId, requestDTO.getChangeQty());

        if (requestDTO.getChangeQty() == null || requestDTO.getChangeQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Stock quantity to reduce must be greater than zero.");
        }

        InventoryEntity inventory = getInventoryOrThrow(productStrId);
        BigDecimal previousStock = inventory.getProductStock() != null ? inventory.getProductStock() : BigDecimal.ZERO;

        if (previousStock.compareTo(requestDTO.getChangeQty()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + previousStock + ", requested reduction: " + requestDTO.getChangeQty());
        }

        BigDecimal newStock = previousStock.subtract(requestDTO.getChangeQty());
        inventory.setProductStock(newStock);
        InventoryEntity saved = inventoryRepository.save(inventory);

        updateProductStatus(inventory.getProduct(), newStock);
        logHistory(inventory.getProduct(), "STOCK_REDUCE", previousStock, newStock, requestDTO.getChangeQty().negate(),
                requestDTO.getRemarks() != null ? requestDTO.getRemarks() : "Stock reduced");

        logger.info("Stock reduced successfully | productStrId={} | newStock={}", productStrId, newStock);
        return mapToResponse(saved);
    }

    // ---------- helpers ----------

    private InventoryEntity getInventoryOrThrow(String productStrId) {
        return inventoryRepository.findByProduct_ProductStrId(productStrId)
                .orElseThrow(() -> {
                    logger.warn("InventoryEntity not found for productStrId: {}", productStrId);
                    return new ResourceNotFoundException("No inventory record found for product ID '" + productStrId + "'.");
                });
    }

    private void updateProductStatus(ProductEntity product, BigDecimal stock) {
        String status = stock.compareTo(BigDecimal.valueOf(20)) > 0 ? "green"
                : stock.compareTo(BigDecimal.valueOf(5)) > 0 ? "amber" : "red";
        product.setProductStatus(status);
        productRepository.save(product);
    }

    private void logHistory(ProductEntity product, String changeType, BigDecimal previousStock,
                            BigDecimal newStock, BigDecimal changeQty, String remarks) {
        InventoryHistoryEntity history = InventoryHistoryEntity.builder()
                .productPrimeId(product.getProductPrimeId())
                .productStrId(product.getProductStrId())
                .historyChangeType(changeType)
                .historyPreviousStock(previousStock)
                .historyNewStock(newStock)
                .historyChangeQty(changeQty)
                .historyRemarks(remarks)
                .build();
        InventoryHistoryEntity saved = inventoryHistoryRepository.save(history);
        saved.setHistoryStrId(StrIdGenerator.generate("HIST", saved.getHistoryPrimeId()));
        inventoryHistoryRepository.save(saved);
    }

    private InventoryResponseDTO mapToResponse(InventoryEntity inventory) {
        return InventoryResponseDTO.builder()
                .inventoryPrimeId(inventory.getInventoryPrimeId())
                .inventoryStrId(inventory.getInventoryStrId())
                .productStrId(inventory.getProduct().getProductStrId())
                .productStock(inventory.getProductStock())
                .inventoryUpdatedAt(inventory.getInventoryUpdatedAt())
                .build();
    }
}