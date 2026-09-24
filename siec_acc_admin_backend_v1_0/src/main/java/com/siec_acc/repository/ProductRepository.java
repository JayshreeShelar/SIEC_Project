package com.siec_acc.repository;


import com.siec_acc.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByProductStrId(String productStrId);
    boolean existsByProductSkuIgnoreCase(String productSku);
}
