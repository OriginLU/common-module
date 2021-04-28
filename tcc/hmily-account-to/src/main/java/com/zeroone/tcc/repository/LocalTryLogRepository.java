package com.zeroone.tcc.repository;

import com.zeroone.tcc.entity.LocalTryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalTryLogRepository extends JpaRepository<LocalTryLog, String> {


    boolean existsByTxNumber(String txNumber);


}