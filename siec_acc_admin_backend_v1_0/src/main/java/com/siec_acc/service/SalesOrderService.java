package com.siec_acc.service;

import com.siec_acc.dto.request.SalesOrderRequestDto;
import com.siec_acc.dto.response.SalesOrderResponseDto;

import java.util.List;

public interface SalesOrderService {

    SalesOrderResponseDto createSalesOrder(SalesOrderRequestDto requestDto);

    SalesOrderResponseDto getSalesOrderById(String id);

    List<SalesOrderResponseDto> getAllSalesOrders();

    SalesOrderResponseDto updateSalesOrder(String id, SalesOrderRequestDto requestDto);

    SalesOrderResponseDto patchSalesOrder(String id, SalesOrderRequestDto requestDto);

    void deleteSalesOrder(String id);
}

