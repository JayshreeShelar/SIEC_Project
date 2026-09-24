package com.siec_acc.service.serviceImpl;

import com.siec_acc.entity.ProductEntity;
import com.siec_acc.exceptions.DuplicateResourceException;
import com.siec_acc.exceptions.ResourceNotFoundException;
import com.siec_acc.utils.StrIdGenerator;

import com.siec_acc.repository.ProductRepository;
import com.siec_acc.dto.request.VariantRequestDTO;
import com.siec_acc.dto.response.VariantResponseDTO;
import com.siec_acc.entity.VariantEntity;
import com.siec_acc.repository.VariantRepository;
import com.siec_acc.service.VariantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class VariantServiceImpl implements VariantService {

    private static final Logger logger = LoggerFactory.getLogger(VariantServiceImpl.class);

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public VariantServiceImpl(VariantRepository variantRepository, ProductRepository productRepository) {
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public VariantResponseDTO createVariant(VariantRequestDTO requestDTO) {
        logger.info("Creating variant for productStrId: {}", requestDTO.getProductStrId());

        ProductEntity product = productRepository.findByProductStrId(requestDTO.getProductStrId())
                .orElseThrow(() -> {
                    logger.warn("Cannot create variant, product not found: {}", requestDTO.getProductStrId());
                    return new ResourceNotFoundException(
                            "No product found with ID '" + requestDTO.getProductStrId() + "' to attach this variant to.");
                });

        if (requestDTO.getVariantSku() != null && !requestDTO.getVariantSku().isBlank()
                && variantRepository.existsByVariantSkuIgnoreCase(requestDTO.getVariantSku())) {
            logger.warn("Duplicate variant SKU: {}", requestDTO.getVariantSku());
            throw new DuplicateResourceException("A variant with SKU '" + requestDTO.getVariantSku() + "' already exists.");
        }

        VariantEntity variant = VariantEntity.builder()
                .product(product)
                .variantName(requestDTO.getVariantName())
                .variantSku(requestDTO.getVariantSku())
                .variantHeight(requestDTO.getVariantHeight())
                .variantWidth(requestDTO.getVariantWidth())
                .variantLength(requestDTO.getVariantLength())
                .variantUnit(requestDTO.getVariantUnit())
                .variantMaterialType(requestDTO.getVariantMaterialType())
                .variantSize(requestDTO.getVariantSize())
                .variantProductNumber(requestDTO.getVariantProductNumber())
                .variantCategory(requestDTO.getVariantCategory())
                .variantSubCategory(requestDTO.getVariantSubCategory())
                .variantStock(requestDTO.getVariantStock() != null ? requestDTO.getVariantStock() : BigDecimal.ZERO)
                .build();

        VariantEntity saved = variantRepository.save(variant);
        saved.setVariantStrId(StrIdGenerator.generate("VAR", saved.getVariantPrimeId()));
        saved = variantRepository.save(saved);

        logger.info("VariantEntity created successfully: {}", saved.getVariantStrId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public VariantResponseDTO updateVariant(String variantStrId, VariantRequestDTO requestDTO) {
        logger.info("Updating variant: {}", variantStrId);
        VariantEntity variant = getVariantOrThrow(variantStrId);
        checkDuplicateSkuOnUpdate(variant, requestDTO.getVariantSku());

        variant.setVariantName(requestDTO.getVariantName());
        variant.setVariantSku(requestDTO.getVariantSku());
        variant.setVariantHeight(requestDTO.getVariantHeight());
        variant.setVariantWidth(requestDTO.getVariantWidth());
        variant.setVariantLength(requestDTO.getVariantLength());
        variant.setVariantUnit(requestDTO.getVariantUnit());
        variant.setVariantMaterialType(requestDTO.getVariantMaterialType());
        variant.setVariantSize(requestDTO.getVariantSize());
        variant.setVariantProductNumber(requestDTO.getVariantProductNumber());
        variant.setVariantCategory(requestDTO.getVariantCategory());
        variant.setVariantSubCategory(requestDTO.getVariantSubCategory());
        if (requestDTO.getVariantStock() != null) variant.setVariantStock(requestDTO.getVariantStock());

        VariantEntity updated = variantRepository.save(variant);
        logger.info("VariantEntity updated successfully: {}", variantStrId);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public VariantResponseDTO patchVariant(String variantStrId, VariantRequestDTO requestDTO) {
        logger.info("Patching variant: {}", variantStrId);
        VariantEntity variant = getVariantOrThrow(variantStrId);
        checkDuplicateSkuOnUpdate(variant, requestDTO.getVariantSku());

        if (requestDTO.getVariantName() != null) variant.setVariantName(requestDTO.getVariantName());
        if (requestDTO.getVariantSku() != null) variant.setVariantSku(requestDTO.getVariantSku());
        if (requestDTO.getVariantHeight() != null) variant.setVariantHeight(requestDTO.getVariantHeight());
        if (requestDTO.getVariantWidth() != null) variant.setVariantWidth(requestDTO.getVariantWidth());
        if (requestDTO.getVariantLength() != null) variant.setVariantLength(requestDTO.getVariantLength());
        if (requestDTO.getVariantUnit() != null) variant.setVariantUnit(requestDTO.getVariantUnit());
        if (requestDTO.getVariantMaterialType() != null) variant.setVariantMaterialType(requestDTO.getVariantMaterialType());
        if (requestDTO.getVariantSize() != null) variant.setVariantSize(requestDTO.getVariantSize());
        if (requestDTO.getVariantProductNumber() != null) variant.setVariantProductNumber(requestDTO.getVariantProductNumber());
        if (requestDTO.getVariantCategory() != null) variant.setVariantCategory(requestDTO.getVariantCategory());
        if (requestDTO.getVariantSubCategory() != null) variant.setVariantSubCategory(requestDTO.getVariantSubCategory());
        if (requestDTO.getVariantStock() != null) variant.setVariantStock(requestDTO.getVariantStock());

        VariantEntity patched = variantRepository.save(variant);
        logger.info("VariantEntity patched successfully: {}", variantStrId);
        return mapToResponse(patched);
    }

    @Override
    @Transactional
    public void deleteVariant(String variantStrId) {
        logger.info("Deleting variant: {}", variantStrId);
        VariantEntity variant = getVariantOrThrow(variantStrId);
        variantRepository.delete(variant);
        logger.info("VariantEntity deleted successfully: {}", variantStrId);
    }

    @Override
    public VariantResponseDTO getVariantByStrId(String variantStrId) {
        logger.info("Fetching variant: {}", variantStrId);
        return mapToResponse(getVariantOrThrow(variantStrId));
    }

    @Override
    public List<VariantResponseDTO> getVariantsByProductStrId(String productStrId) {
        logger.info("Fetching variants for productStrId: {}", productStrId);
        if (productRepository.findByProductStrId(productStrId).isEmpty()) {
            throw new ResourceNotFoundException("No product found with ID '" + productStrId + "'.");
        }
        return variantRepository.findByProduct_ProductStrId(productStrId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------- helpers ----------

    private VariantEntity getVariantOrThrow(String variantStrId) {
        return variantRepository.findByVariantStrId(variantStrId)
                .orElseThrow(() -> {
                    logger.warn("VariantEntity not found: {}", variantStrId);
                    return new ResourceNotFoundException("No variant found with ID '" + variantStrId + "'.");
                });
    }

    private void checkDuplicateSkuOnUpdate(VariantEntity existing, String newSku) {
        if (newSku != null && !newSku.isBlank()
                && !newSku.equalsIgnoreCase(existing.getVariantSku())
                && variantRepository.existsByVariantSkuIgnoreCase(newSku)) {
            logger.warn("Duplicate variant SKU on update: {}", newSku);
            throw new DuplicateResourceException("A variant with SKU '" + newSku + "' already exists.");
        }
    }

    private VariantResponseDTO mapToResponse(VariantEntity variant) {
        return VariantResponseDTO.builder()
                .variantPrimeId(variant.getVariantPrimeId())
                .variantStrId(variant.getVariantStrId())
                .productStrId(variant.getProduct().getProductStrId())
                .variantName(variant.getVariantName())
                .variantSku(variant.getVariantSku())
                .variantHeight(variant.getVariantHeight())
                .variantWidth(variant.getVariantWidth())
                .variantLength(variant.getVariantLength())
                .variantUnit(variant.getVariantUnit())
                .variantMaterialType(variant.getVariantMaterialType())
                .variantSize(variant.getVariantSize())
                .variantProductNumber(variant.getVariantProductNumber())
                .variantCategory(variant.getVariantCategory())
                .variantSubCategory(variant.getVariantSubCategory())
                .variantStock(variant.getVariantStock())
                .variantCreatedAt(variant.getVariantCreatedAt())
                .variantUpdatedAt(variant.getVariantUpdatedAt())
                .build();
    }
}