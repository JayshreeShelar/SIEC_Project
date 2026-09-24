package com.siec_acc.repository;

import com.siec_acc.entity.InventoryHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistoryEntity, Long> {
    List<InventoryHistoryEntity> findByProductStrIdOrderByHistoryCreatedAtDesc(String productStrId);
    List<InventoryHistoryEntity> findByVariantStrIdOrderByHistoryCreatedAtDesc(String variantStrId);
}