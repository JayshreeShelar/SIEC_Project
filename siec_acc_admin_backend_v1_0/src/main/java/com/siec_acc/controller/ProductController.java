package com.siec_acc.controller;

import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.dto.request.ProductRequestDTO;
import com.siec_acc.dto.response.ProductResponseDTO;
import com.siec_acc.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/v1")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @PostMapping("/create-product")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody ProductRequestDTO requestDTO) {
        logger.info("API HIT: POST /create-product | productName={}", requestDTO.getProductName());
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product created successfully.", response));
    }

    @PutMapping("/update-product/{productStrId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable String productStrId, @RequestBody ProductRequestDTO requestDTO) {
        logger.info("API HIT: PUT /update-product/{}", productStrId);
        ProductResponseDTO response = productService.updateProduct(productStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully.", response));
    }

    @PatchMapping("/patch-product/{productStrId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> patchProduct(
            @PathVariable String productStrId, @RequestBody ProductRequestDTO requestDTO) {
        logger.info("API HIT: PATCH /patch-product/{}", productStrId);
        ProductResponseDTO response = productService.patchProduct(productStrId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully.", response));
    }

    @DeleteMapping("/delete-product/{productStrId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String productStrId) {
        logger.info("API HIT: DELETE /delete-product/{}", productStrId);
        productService.deleteProduct(productStrId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully.", null));
    }

    @GetMapping("/get-product/{productStrId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProduct(@PathVariable String productStrId) {
        logger.info("API HIT: GET /get-product/{}", productStrId);
        ProductResponseDTO response = productService.getProductByStrId(productStrId);
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully.", response));
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        logger.info("API HIT: GET /get-all-products");
        List<ProductResponseDTO> response = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully.", response));
    }
}