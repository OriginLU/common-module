package com.zeroone.tcc.repository;

import com.zeroone.tcc.entity.LocalConfirmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalConfirmLogRepository extends JpaRepository<LocalConfirmLog, String> {


    boolean existsByTxNumber(String txNumber);
}