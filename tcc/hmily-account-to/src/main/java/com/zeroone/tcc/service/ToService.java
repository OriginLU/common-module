package com.zeroone.tcc.service;

import com.zeroone.tcc.repository.AccountRepository;
import org.dromara.hmily.annotation.Hmily;
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

    @Transactional(rollbackFor = Exception.class)
    @Hmily(confirmMethod = "confirmUpdate",cancelMethod = "cancelUpdate")
    public void tryUpdate(Long id,Long transMoney){
        log.info("加钱准备");
    }


    @Transactional(rollbackFor = Exception.class)
    public void confirmUpdate(Long id,Long transMoney){
        accountRepository.updateBalance(id,transMoney);
        log.info("加钱确认");
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelUpdate(Long id,Long transMoney){
        log.info("加钱取消");
    }
}
