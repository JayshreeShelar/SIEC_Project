package com.siec_acc.service;

import com.siec_acc.dto.request.PurchaseBillPatchDto;
import com.siec_acc.dto.request.PurchaseBillPaymentRequestDto;
import com.siec_acc.dto.request.PurchaseBillRequestDto;
import com.siec_acc.dto.response.PurchaseBillItemResponseDto;
import com.siec_acc.dto.response.PurchaseBillResponseDto;

import java.util.List;

public interface PurchaseBillService {

    PurchaseBillResponseDto createPurchaseBill(PurchaseBillRequestDto requestDto);

    PurchaseBillResponseDto updatePurchaseBill(String pbStrId, PurchaseBillRequestDto requestDto);

    PurchaseBillResponseDto patchPurchaseBill(String pbStrId, PurchaseBillPatchDto patchDto);

    void deletePurchaseBill(String pbStrId);

    PurchaseBillResponseDto getPurchaseBillByStrId(String pbStrId);

    List<PurchaseBillResponseDto> getAllPurchaseBills();

    List<PurchaseBillResponseDto> getPurchaseBillsByStatus(String status);

    PurchaseBillResponseDto recordPayment(String pbStrId, PurchaseBillPaymentRequestDto paymentDto);

    PurchaseBillResponseDto deletePayment(String pbStrId, Long paymentId);

    List<PurchaseBillItemResponseDto> getBillableItems(String poNumber, String excludePbStrId);
}


