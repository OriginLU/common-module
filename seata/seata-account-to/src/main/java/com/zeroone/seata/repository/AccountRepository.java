package com.zeroone.seata.repository;

import com.zeroone.seata.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {



    @Modifying
    @Query("update Account set balance = balance + ?2 where id = ?1")
    Integer updateBalance(Long id,Long balance);
}
