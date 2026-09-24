package com.siec_acc.service;

import com.siec_acc.dto.request.ProductRequestDTO;
import com.siec_acc.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(String productStrId, ProductRequestDTO requestDTO);
    ProductResponseDTO patchProduct(String productStrId, ProductRequestDTO requestDTO);
    void deleteProduct(String productStrId);
    ProductResponseDTO getProductByStrId(String productStrId);
    List<ProductResponseDTO> getAllProducts();
}