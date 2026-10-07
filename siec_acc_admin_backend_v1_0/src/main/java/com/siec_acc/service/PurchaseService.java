package com.siec_acc.service;

import com.siec_acc.dto.request.PurchasePatchDto;
import com.siec_acc.dto.request.PurchaseRequestDto;
import com.siec_acc.dto.response.PurchaseResponseDto;

import java.util.List;

public interface PurchaseService {
    PurchaseResponseDto createPurchase(PurchaseRequestDto requestDto);
    PurchaseResponseDto updatePurchase(String purchaseStrId, PurchaseRequestDto requestDto);
    PurchaseResponseDto patchPurchase(String purchaseStrId, PurchasePatchDto patchDto);
    void deletePurchase(String purchaseStrId);
    PurchaseResponseDto getPurchaseByStrId(String purchaseStrId);
    List<PurchaseResponseDto> getAllPurchases();
    List<PurchaseResponseDto> getPurchasesByStatus(String status);
    List<PurchaseResponseDto> getPurchasesByDepartment(String department);
    PurchaseResponseDto approvePurchase(String purchaseStrId);
    PurchaseResponseDto rejectPurchase(String purchaseStrId, String reason);
}