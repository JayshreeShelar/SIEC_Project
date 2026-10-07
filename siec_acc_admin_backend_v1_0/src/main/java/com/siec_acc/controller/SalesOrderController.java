package com.siec_acc.controller;


import com.siec_acc.dto.request.SalesOrderRequestDto;
import com.siec_acc.dto.response.SalesOrderResponseDto;
import com.siec_acc.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping("/create-sales-order")
    public ResponseEntity<SalesOrderResponseDto> createSalesOrder(@RequestBody SalesOrderRequestDto requestDto) {
        SalesOrderResponseDto response = salesOrderService.createSalesOrder(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-sales-order-by-id/{id}")
    public ResponseEntity<SalesOrderResponseDto> getSalesOrderById(@PathVariable String id) {
        SalesOrderResponseDto response = salesOrderService.getSalesOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-sales-orders")
    public ResponseEntity<List<SalesOrderResponseDto>> getAllSalesOrders() {
        List<SalesOrderResponseDto> response = salesOrderService.getAllSalesOrders();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-sales-order/{id}")
    public ResponseEntity<SalesOrderResponseDto> updateSalesOrder(@PathVariable String id,
                                                                  @RequestBody SalesOrderRequestDto requestDto) {
        SalesOrderResponseDto response = salesOrderService.updateSalesOrder(id, requestDto);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/patch-sales-order/{id}")
    public ResponseEntity<SalesOrderResponseDto> patchSalesOrder(@PathVariable String id,
                                                                 @RequestBody SalesOrderRequestDto requestDto) {
        SalesOrderResponseDto response = salesOrderService.patchSalesOrder(id, requestDto);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete-sales-order/{id}")
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable String id) {
        salesOrderService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}


