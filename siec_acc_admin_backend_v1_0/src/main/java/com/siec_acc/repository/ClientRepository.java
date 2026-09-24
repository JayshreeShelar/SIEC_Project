package com.siec_acc.repository;

import com.siec_acc.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long>,
        JpaSpecificationExecutor<ClientEntity> {

    Optional<ClientEntity> findByClientStrId(String clientStrId);

    boolean existsByClientEmailIgnoreCase(String clientEmail);

    boolean existsByClientGstinIgnoreCase(String clientGstin);
}