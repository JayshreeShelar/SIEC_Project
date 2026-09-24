package com.siec_acc.controller;


import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.dto.request.VariantRequestDTO;
import com.siec_acc.dto.response.VariantResponseDTO;
import com.siec_acc.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variants/v1")
@RequiredArgsConstructor
public class VariantController {

    private static final Logger logger = LoggerFactory.getLogger(VariantController.class);
    private final VariantService variantService;

    @PostMapping("/create-variant")
    public ResponseEntity<ApiResponse<VariantResponseDTO>> createVariant(@RequestBody VariantRequestDTO requestDTO) {
        logger.info("API HIT: POST /create-variant | productStrId={}", requestDTO.getProductStrId());
        VariantResponseDTO response = variantService.createVariant(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Variant created successfully.", response));
    }

    @PutMapping("/update-variant/{variantStrId}")
    public ResponseEntity<ApiResponse<VariantResponseDTO>> updateVariant(
            @PathVariable String variantStrId, @RequestBody VariantRequestDTO requestDTO) {
        logger.info("API HIT: PUT /update-variant/{}", variantStrId);
        VariantResponseDTO response = variantService.updateVariant(variantStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Variant updated successfully.", response));
    }

    @PatchMapping("/patch-variant/{variantStrId}")
    public ResponseEntity<ApiResponse<VariantResponseDTO>> patchVariant(
            @PathVariable String variantStrId, @RequestBody VariantRequestDTO requestDTO) {
        logger.info("API HIT: PATCH /patch-variant/{}", variantStrId);
        VariantResponseDTO response = variantService.patchVariant(variantStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Variant updated successfully.", response));
    }

    @DeleteMapping("/delete-variant/{variantStrId}")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable String variantStrId) {
        logger.info("API HIT: DELETE /delete-variant/{}", variantStrId);
        variantService.deleteVariant(variantStrId);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted successfully.", null));
    }

    @GetMapping("/get-variant/{variantStrId}")
    public ResponseEntity<ApiResponse<VariantResponseDTO>> getVariant(@PathVariable String variantStrId) {
        logger.info("API HIT: GET /get-variant/{}", variantStrId);
        VariantResponseDTO response = variantService.getVariantByStrId(variantStrId);
        return ResponseEntity.ok(ApiResponse.success("Variant fetched successfully.", response));
    }

    @GetMapping("/get-variants-by-product/{productStrId}")
    public ResponseEntity<ApiResponse<List<VariantResponseDTO>>> getVariantsByProduct(@PathVariable String productStrId) {
        logger.info("API HIT: GET /get-variants-by-product/{}", productStrId);
        List<VariantResponseDTO> response = variantService.getVariantsByProductStrId(productStrId);
        return ResponseEntity.ok(ApiResponse.success("Variants fetched successfully.", response));
    }
}