package com.siec_acc.service;

import com.siec_acc.dto.request.VariantRequestDTO;
import com.siec_acc.dto.response.VariantResponseDTO;

import java.util.List;

public interface VariantService {
    VariantResponseDTO createVariant(VariantRequestDTO requestDTO);
    VariantResponseDTO updateVariant(String variantStrId, VariantRequestDTO requestDTO);
    VariantResponseDTO patchVariant(String variantStrId, VariantRequestDTO requestDTO);
    void deleteVariant(String variantStrId);
    VariantResponseDTO getVariantByStrId(String variantStrId);
    List<VariantResponseDTO> getVariantsByProductStrId(String productStrId);
}