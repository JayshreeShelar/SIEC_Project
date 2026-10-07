package com.siec_acc.controller;

import com.siec_acc.dto.request.PurchasePatchDto;
import com.siec_acc.dto.request.PurchaseRequestDto;
import com.siec_acc.dto.request.RejectPurchaseRequestDto;
import com.siec_acc.dto.response.PurchaseResponseDto;
import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.service.PurchaseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases/v1")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/create-purchase")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> createPurchase(@Valid @RequestBody PurchaseRequestDto requestDto) {
        logger.info("API HIT: POST /create-purchase | itemName={}", requestDto.getItemName());
        PurchaseResponseDto response = purchaseService.createPurchase(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Purchase requirement created successfully.", response));
    }

    @PutMapping("/update-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> updatePurchase(
            @PathVariable String purchaseStrId, @Valid @RequestBody PurchaseRequestDto requestDto) {
        logger.info("API HIT: PUT /update-purchase/{}", purchaseStrId);
        PurchaseResponseDto response = purchaseService.updatePurchase(purchaseStrId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement updated successfully.", response));
    }

    @PatchMapping("/patch-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> patchPurchase(
            @PathVariable String purchaseStrId, @Valid @RequestBody PurchasePatchDto patchDto) {
        logger.info("API HIT: PATCH /patch-purchase/{}", purchaseStrId);
        PurchaseResponseDto response = purchaseService.patchPurchase(purchaseStrId, patchDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement updated successfully.", response));
    }

    @DeleteMapping("/delete-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase(@PathVariable String purchaseStrId) {
        logger.info("API HIT: DELETE /delete-purchase/{}", purchaseStrId);
        purchaseService.deletePurchase(purchaseStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement deleted successfully.", null));
    }

    @GetMapping("/get-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> getPurchase(@PathVariable String purchaseStrId) {
        logger.info("API HIT: GET /get-purchase/{}", purchaseStrId);
        PurchaseResponseDto response = purchaseService.getPurchaseByStrId(purchaseStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement fetched successfully.", response));
    }

    @GetMapping("/get-all-purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> getAllPurchases() {
        logger.info("API HIT: GET /get-all-purchases");
        List<PurchaseResponseDto> response = purchaseService.getAllPurchases();
        return ResponseEntity.ok(ApiResponse.success("Purchase requirements fetched successfully.", response));
    }

    @GetMapping("/get-by-status/{status}")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> getByStatus(@PathVariable String status) {
        logger.info("API HIT: GET /get-by-status/{}", status);
        List<PurchaseResponseDto> response = purchaseService.getPurchasesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirements fetched successfully.", response));
    }

    @GetMapping("/get-by-department/{department}")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> getByDepartment(@PathVariable String department) {
        logger.info("API HIT: GET /get-by-department/{}", department);
        List<PurchaseResponseDto> response = purchaseService.getPurchasesByDepartment(department);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirements fetched successfully.", response));
    }

    @PatchMapping("/approve-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> approvePurchase(@PathVariable String purchaseStrId) {
        logger.info("API HIT: PATCH /approve-purchase/{}", purchaseStrId);
        PurchaseResponseDto response = purchaseService.approvePurchase(purchaseStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement approved successfully.", response));
    }

    // Reason now travels in the request body (optional) instead of the URL query string.
    @PatchMapping("/reject-purchase/{purchaseStrId}")
    public ResponseEntity<ApiResponse<PurchaseResponseDto>> rejectPurchase(
            @PathVariable String purchaseStrId,
            @Valid @RequestBody(required = false) RejectPurchaseRequestDto body) {
        logger.info("API HIT: PATCH /reject-purchase/{}", purchaseStrId);
        String reason = body != null ? body.getReason() : null;
        PurchaseResponseDto response = purchaseService.rejectPurchase(purchaseStrId, reason);
        return ResponseEntity.ok(ApiResponse.success("Purchase requirement rejected successfully.", response));
    }
}

