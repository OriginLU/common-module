package com.zeroone.seata.service;

import com.zeroone.seata.repository.AccountRepository;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ToService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository accountRepository;


    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public void tryUpdate(Long id,Long transMoney){
        if (accountRepository.updateBalance(id,transMoney) <= 0) {
            log.info("加钱失败");
            throw new RuntimeException("加钱失败");
        }
    }

}
