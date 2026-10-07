package com.siec_acc.repository;
import com.siec_acc.entity.PurchaseBillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseBillRepository extends JpaRepository<PurchaseBillEntity, Long> {
    Optional<PurchaseBillEntity> findByPbStrId(String pbStrId);
    Optional<PurchaseBillEntity> findByPbNumber(String pbNumber);

    boolean existsByPoNumberIgnoreCase(String poNumber);

    List<PurchaseBillEntity> findAllByOrderByCreatedAtDesc();
    List<PurchaseBillEntity> findByVendorNameContainingIgnoreCaseOrderByCreatedAtDesc(String vendorName);
    List<PurchaseBillEntity> findByPoNumberIgnoreCaseOrderByCreatedAtDesc(String poNumber);
}

