package com.siec_acc.repository;

import com.siec_acc.entity.SalesOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrderEntity, String> {

    /** All primary keys (SO-001, SO-002 ...) - used to find the highest sequence in use. */
    @Query("select s.id from SalesOrderEntity s")
    List<String> findAllIds();

    /** All order numbers of one financial year, e.g. prefix "SO/26-27/". */
    @Query("select s.number from SalesOrderEntity s where s.number like concat(:prefix, '%')")
    List<String> findNumbersByPrefix(@Param("prefix") String prefix);
}
