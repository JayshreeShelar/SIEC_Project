package com.siec_acc.repository;

import com.siec_acc.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity> findByAdminId(String adminId);
    Optional<AdminEntity> findByAdminMobileNumber(String mobileNumber);
    boolean existsByAdminId(String adminId);
    boolean existsByAdminMobileNumber(String mobileNumber);
    boolean existsByAdminRole(String role);
}