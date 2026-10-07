package com.siec_acc.service;

import com.siec_acc.dto.request.PurchaseOrderPatchDto;
import com.siec_acc.dto.request.PurchaseOrderRequestDto;
import com.siec_acc.dto.response.PurchaseOrderResponseDto;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponseDto createPurchaseOrder(PurchaseOrderRequestDto requestDto);
    PurchaseOrderResponseDto updatePurchaseOrder(String poStrId, PurchaseOrderRequestDto requestDto);
    PurchaseOrderResponseDto patchPurchaseOrder(String poStrId, PurchaseOrderPatchDto patchDto);
    void deletePurchaseOrder(String poStrId);
    PurchaseOrderResponseDto getPurchaseOrderByStrId(String poStrId);
    List<PurchaseOrderResponseDto> getAllPurchaseOrders();
    List<PurchaseOrderResponseDto> getPurchaseOrdersByStatus(String status);
    List<PurchaseOrderResponseDto> getPurchaseOrdersByVendor(String vendorName);
}
