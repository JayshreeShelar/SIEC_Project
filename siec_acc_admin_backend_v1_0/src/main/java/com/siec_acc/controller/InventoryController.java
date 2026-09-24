package com.siec_acc.controller;

import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.dto.response.InventoryResponseDTO;
import com.siec_acc.dto.request.InventoryStockUpdateDTO;
import com.siec_acc.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/v1")
@RequiredArgsConstructor
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;

    @GetMapping("/get-inventory/{productStrId}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> getInventory(@PathVariable String productStrId) {
        logger.info("API HIT: GET /get-inventory/{}", productStrId);
        InventoryResponseDTO response = inventoryService.getInventoryByProductStrId(productStrId);
        return ResponseEntity.ok(ApiResponse.success("Inventory fetched successfully.", response));
    }

    // used by "Vendor Purchase -> Existing Item" flow on the frontend
    @PatchMapping("/add-stock/{productStrId}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> addStock(
            @PathVariable String productStrId, @RequestBody InventoryStockUpdateDTO requestDTO) {
        logger.info("API HIT: PATCH /add-stock/{}", productStrId);
        InventoryResponseDTO response = inventoryService.addStock(productStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Stock added successfully.", response));
    }

    @PatchMapping("/reduce-stock/{productStrId}")
    public ResponseEntity<ApiResponse<InventoryResponseDTO>> reduceStock(
            @PathVariable String productStrId, @RequestBody InventoryStockUpdateDTO requestDTO) {
        logger.info("API HIT: PATCH /reduce-stock/{}", productStrId);
        InventoryResponseDTO response = inventoryService.reduceStock(productStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Stock reduced successfully.", response));
    }
}