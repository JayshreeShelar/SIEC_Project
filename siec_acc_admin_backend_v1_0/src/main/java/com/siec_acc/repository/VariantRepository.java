package com.siec_acc.repository;

import com.siec_acc.entity.VariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VariantRepository extends JpaRepository<VariantEntity, Long> {
    Optional<VariantEntity> findByVariantStrId(String variantStrId);
    List<VariantEntity> findByProduct_ProductStrId(String productStrId);
    boolean existsByVariantSkuIgnoreCase(String variantSku);
}