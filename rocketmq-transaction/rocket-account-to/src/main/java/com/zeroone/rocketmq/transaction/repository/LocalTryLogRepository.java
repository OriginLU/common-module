package com.zeroone.rocketmq.transaction.repository;

import com.zeroone.rocketmq.transaction.entity.LocalTryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalTryLogRepository extends JpaRepository<LocalTryLog, String> {


    boolean existsByTxNumber(String txNumber);

}