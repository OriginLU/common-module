package com.zeroon.rocketmq.transaction.repository;

import com.zeroon.rocketmq.transaction.entity.LocalTryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalTryLogRepository extends JpaRepository<LocalTryLog, String> {


    boolean existsByTxNumber(String txNumber);


}