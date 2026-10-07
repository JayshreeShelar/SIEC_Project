package com.siec_acc.controller;

import com.siec_acc.dto.request.PurchaseBillPatchDto;
import com.siec_acc.dto.request.PurchaseBillPaymentRequestDto;
import com.siec_acc.dto.request.PurchaseBillRequestDto;
import com.siec_acc.dto.response.PurchaseBillItemResponseDto;
import com.siec_acc.dto.response.PurchaseBillResponseDto;
import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.service.PurchaseBillService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-bills/v1")
public class PurchaseBillController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseBillController.class);

    private final PurchaseBillService purchaseBillService;

    public PurchaseBillController(PurchaseBillService purchaseBillService) {
        this.purchaseBillService = purchaseBillService;
    }

    @PostMapping("/create-purchase-bill")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> createPurchaseBill(@Valid @RequestBody PurchaseBillRequestDto requestDto) {
        logger.info("API HIT: POST /create-purchase-bill | vendor={}", requestDto.getVendorName());
        PurchaseBillResponseDto response = purchaseBillService.createPurchaseBill(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Purchase bill created successfully.", response));
    }

    @PutMapping("/update-purchase-bill/{pbStrId}")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> updatePurchaseBill(
            @PathVariable String pbStrId, @Valid @RequestBody PurchaseBillRequestDto requestDto) {
        logger.info("API HIT: PUT /update-purchase-bill/{}", pbStrId);
        PurchaseBillResponseDto response = purchaseBillService.updatePurchaseBill(pbStrId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase bill updated successfully.", response));
    }

    @PatchMapping("/patch-purchase-bill/{pbStrId}")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> patchPurchaseBill(
            @PathVariable String pbStrId, @Valid @RequestBody PurchaseBillPatchDto patchDto) {
        logger.info("API HIT: PATCH /patch-purchase-bill/{}", pbStrId);
        PurchaseBillResponseDto response = purchaseBillService.patchPurchaseBill(pbStrId, patchDto);
        return ResponseEntity.ok(ApiResponse.success("Purchase bill updated successfully.", response));
    }

    @DeleteMapping("/delete-purchase-bill/{pbStrId}")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseBill(@PathVariable String pbStrId) {
        logger.info("API HIT: DELETE /delete-purchase-bill/{}", pbStrId);
        purchaseBillService.deletePurchaseBill(pbStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase bill deleted successfully.", null));
    }

    @GetMapping("/get-purchase-bill/{pbStrId}")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> getPurchaseBill(@PathVariable String pbStrId) {
        logger.info("API HIT: GET /get-purchase-bill/{}", pbStrId);
        PurchaseBillResponseDto response = purchaseBillService.getPurchaseBillByStrId(pbStrId);
        return ResponseEntity.ok(ApiResponse.success("Purchase bill fetched successfully.", response));
    }

    @GetMapping("/get-all-purchase-bills")
    public ResponseEntity<ApiResponse<List<PurchaseBillResponseDto>>> getAllPurchaseBills() {
        logger.info("API HIT: GET /get-all-purchase-bills");
        List<PurchaseBillResponseDto> response = purchaseBillService.getAllPurchaseBills();
        return ResponseEntity.ok(ApiResponse.success("Purchase bills fetched successfully.", response));
    }

    // Status here is DERIVED (received/partial/paid/overdue) - not a stored column - same convention as the frontend.
    @GetMapping("/get-by-status/{status}")
    public ResponseEntity<ApiResponse<List<PurchaseBillResponseDto>>> getByStatus(@PathVariable String status) {
        logger.info("API HIT: GET /get-by-status/{}", status);
        List<PurchaseBillResponseDto> response = purchaseBillService.getPurchaseBillsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Purchase bills fetched successfully.", response));
    }

    @PostMapping("/record-payment/{pbStrId}")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> recordPayment(
            @PathVariable String pbStrId, @Valid @RequestBody PurchaseBillPaymentRequestDto paymentDto) {
        logger.info("API HIT: POST /record-payment/{}", pbStrId);
        PurchaseBillResponseDto response = purchaseBillService.recordPayment(pbStrId, paymentDto);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully.", response));
    }

    @DeleteMapping("/delete-payment/{pbStrId}/{paymentId}")
    public ResponseEntity<ApiResponse<PurchaseBillResponseDto>> deletePayment(
            @PathVariable String pbStrId, @PathVariable Long paymentId) {
        logger.info("API HIT: DELETE /delete-payment/{}/{}", pbStrId, paymentId);
        PurchaseBillResponseDto response = purchaseBillService.deletePayment(pbStrId, paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully.", response));
    }

    // Items still billable on a PO (accepted goods received - already billed). Used to pre-fill a new bill.
    @GetMapping("/billable-items/{poNumber}")
    public ResponseEntity<ApiResponse<List<PurchaseBillItemResponseDto>>> getBillableItems(
            @PathVariable String poNumber, @RequestParam(required = false) String excludePbStrId) {
        logger.info("API HIT: GET /billable-items/{}", poNumber);
        List<PurchaseBillItemResponseDto> response = purchaseBillService.getBillableItems(poNumber, excludePbStrId);
        return ResponseEntity.ok(ApiResponse.success("Billable items fetched successfully.", response));
    }
}

