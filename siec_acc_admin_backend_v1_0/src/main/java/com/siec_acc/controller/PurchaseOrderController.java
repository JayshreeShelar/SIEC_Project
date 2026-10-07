package com.siec_acc.controller;

import com.siec_acc.dto.request.PurchaseOrderPatchDto;
import com.siec_acc.dto.request.PurchaseOrderRequestDto;
import com.siec_acc.dto.response.PurchaseOrderResponseDto;
import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders/v1")
public class PurchaseOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);
    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping("/create-purchase-order")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDto requestDto) {
        logger.info("API HIT: POST /create-purchase-order | vendor={}", requestDto.getVendorName());
        PurchaseOrderResponseDto response = purchaseOrderService.createPurchaseOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Purchase order created successfully.", response));
    }

    @PutMapping("/update-purchase-order/{poStrId}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> updatePurchaseOrder(
            @PathVariable String poStrId, @Valid @RequestBody PurchaseOrderRequestDto requestDto) {
        logger.info("API HIT: PUT /update-purchase-order/{}", poStrId);
        PurchaseOrderResponseDto response = purchaseOrderService.updatePurchaseOrder(poStrId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase order updated successfully.", response));
    }

    @PatchMapping("/patch-purchase-order/{poStrId}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> patchPurchaseOrder(
            @PathVariable String poStrId, @Valid @RequestBody PurchaseOrderPatchDto patchDto) {
        logger.info("API HIT: PATCH /patch-purchase-order/{}", poStrId);
        PurchaseOrderResponseDto response = purchaseOrderService.patchPurchaseOrder(poStrId, patchDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase order updated successfully.", response));
    }

    @DeleteMapping("/delete-purchase-order/{poStrId}")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(@PathVariable String poStrId) {
        logger.info("API HIT: DELETE /delete-purchase-order/{}", poStrId);
        purchaseOrderService.deletePurchaseOrder(poStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase order deleted successfully.", null));
    }

    @GetMapping("/get-purchase-order/{poStrId}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> getPurchaseOrder(@PathVariable String poStrId) {
        logger.info("API HIT: GET /get-purchase-order/{}", poStrId);
        PurchaseOrderResponseDto response = purchaseOrderService.getPurchaseOrderByStrId(poStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase order fetched successfully.", response));
    }

    @GetMapping("/get-all-purchase-orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponseDto>>> getAllPurchaseOrders() {
        logger.info("API HIT: GET /get-all-purchase-orders");
        List<PurchaseOrderResponseDto> response = purchaseOrderService.getAllPurchaseOrders();
        return ResponseEntity.ok(ApiResponse.success("Purchase orders fetched successfully.", response));
    }

    @GetMapping("/get-by-status/{status}")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponseDto>>> getByStatus(@PathVariable String status) {
        logger.info("API HIT: GET /get-by-status/{}", status);
        List<PurchaseOrderResponseDto> response = purchaseOrderService.getPurchaseOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders fetched successfully.", response));
    }

    @GetMapping("/get-by-vendor/{vendorName}")
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponseDto>>> getByVendor(@PathVariable String vendorName) {
        logger.info("API HIT: GET /get-by-vendor/{}", vendorName);
        List<PurchaseOrderResponseDto> response = purchaseOrderService.getPurchaseOrdersByVendor(vendorName);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders fetched successfully.", response));
    }
}

