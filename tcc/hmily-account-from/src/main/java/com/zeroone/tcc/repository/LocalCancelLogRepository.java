package com.zeroone.tcc.repository;

import com.zeroone.tcc.entity.LocalCancelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalCancelLogRepository extends JpaRepository<LocalCancelLog, String> {



    boolean existsByTxNumber(String txNumber);

}