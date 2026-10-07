package com.siec_acc.controller;

import com.siec_acc.dto.request.GoodsReceiptRequestDto;
import com.siec_acc.dto.response.GoodsReceiptResponseDto;
import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods-receipts/v1")
public class GoodsReceiptController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsReceiptController.class);
    private final GoodsReceiptService goodsReceiptService;

    public GoodsReceiptController(GoodsReceiptService goodsReceiptService) {
        this.goodsReceiptService = goodsReceiptService;
    }

    @PostMapping("/create-goods-receipt")
    public ResponseEntity<ApiResponse<GoodsReceiptResponseDto>> create(@Valid @RequestBody GoodsReceiptRequestDto requestDto) {
        logger.info("API HIT: POST /create-goods-receipt | po={}", requestDto.getPoStrId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Goods receipt created successfully.", goodsReceiptService.createGoodsReceipt(requestDto)));
    }

    @PutMapping("/update-goods-receipt/{grnStrId}")
    public ResponseEntity<ApiResponse<GoodsReceiptResponseDto>> update(
            @PathVariable String grnStrId, @Valid @RequestBody GoodsReceiptRequestDto requestDto) {
        logger.info("API HIT: PUT /update-goods-receipt/{}", grnStrId);
        return ResponseEntity.ok(ApiResponse.success("Goods receipt updated successfully.", goodsReceiptService.updateGoodsReceipt(grnStrId, requestDto)));
    }

    @DeleteMapping("/delete-goods-receipt/{grnStrId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String grnStrId) {
        logger.info("API HIT: DELETE /delete-goods-receipt/{}", grnStrId);
        goodsReceiptService.deleteGoodsReceipt(grnStrId);
        return ResponseEntity.ok(ApiResponse.success("Goods receipt deleted successfully.", null));
    }

    @GetMapping("/get-goods-receipt/{grnStrId}")
    public ResponseEntity<ApiResponse<GoodsReceiptResponseDto>> getOne(@PathVariable String grnStrId) {
        logger.info("API HIT: GET /get-goods-receipt/{}", grnStrId);
        return ResponseEntity.ok(ApiResponse.success("Goods receipt fetched successfully.", goodsReceiptService.getGoodsReceiptByStrId(grnStrId)));
    }

    @GetMapping("/get-all-goods-receipts")
    public ResponseEntity<ApiResponse<List<GoodsReceiptResponseDto>>> getAll() {
        logger.info("API HIT: GET /get-all-goods-receipts");
        return ResponseEntity.ok(ApiResponse.success("Goods receipts fetched successfully.", goodsReceiptService.getAllGoodsReceipts()));
    }

    @GetMapping("/get-by-po/{poStrId}")
    public ResponseEntity<ApiResponse<List<GoodsReceiptResponseDto>>> getByPo(@PathVariable String poStrId) {
        logger.info("API HIT: GET /get-by-po/{}", poStrId);
        return ResponseEntity.ok(ApiResponse.success("Goods receipts fetched successfully.", goodsReceiptService.getGoodsReceiptsByPo(poStrId)));
    }
}


