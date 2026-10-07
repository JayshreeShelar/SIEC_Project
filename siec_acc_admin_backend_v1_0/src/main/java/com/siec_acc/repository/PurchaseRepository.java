package com.siec_acc.repository;

import com.siec_acc.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
    Optional<PurchaseEntity> findByPurchaseStrId(String purchaseStrId);
    Optional<PurchaseEntity> findByPurchaseNumber(String purchaseNumber);
    boolean existsByPurchaseNumberIgnoreCase(String purchaseNumber);
    List<PurchaseEntity> findAllByOrderByCreatedAtDesc();
    List<PurchaseEntity> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<PurchaseEntity> findByDepartmentIgnoreCaseOrderByCreatedAtDesc(String department);
    Optional<PurchaseEntity> findByConvertedPoNumberIgnoreCase(String convertedPoNumber);
}