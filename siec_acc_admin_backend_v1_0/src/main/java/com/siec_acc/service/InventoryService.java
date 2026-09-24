package com.siec_acc.service;

import com.siec_acc.dto.response.InventoryResponseDTO;
import com.siec_acc.dto.request.InventoryStockUpdateDTO;

public interface InventoryService {
    InventoryResponseDTO getInventoryByProductStrId(String productStrId);
    InventoryResponseDTO addStock(String productStrId, InventoryStockUpdateDTO requestDTO);
    InventoryResponseDTO reduceStock(String productStrId, InventoryStockUpdateDTO requestDTO);
}