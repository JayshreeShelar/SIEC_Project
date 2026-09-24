package com.siec_acc.repository;

import com.siec_acc.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    Optional<InventoryEntity> findByProduct_ProductStrId(String productStrId);
    Optional<InventoryEntity> findByProduct_ProductPrimeId(Long productPrimeId);
}