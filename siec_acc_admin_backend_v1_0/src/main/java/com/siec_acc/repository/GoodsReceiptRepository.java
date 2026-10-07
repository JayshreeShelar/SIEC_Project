package com.siec_acc.repository;

import com.siec_acc.entity.GoodsReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceiptEntity, Long> {
    Optional<GoodsReceiptEntity> findByGrnStrId(String grnStrId);

    boolean existsByPoStrId(String poStrId);
    boolean existsByPoNumberIgnoreCase(String poNumber);

    List<GoodsReceiptEntity> findByPoStrIdOrderByGrnPrimeIdAsc(String poStrId);

    List<GoodsReceiptEntity> findAllByOrderByCreatedAtDesc();
}
