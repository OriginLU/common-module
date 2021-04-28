package com.zeroone.seata.service;

import com.zeroone.seata.repository.AccountRepository;
//import com.zeroone.tcc.proxy.ToAccountProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FromService {

    private final Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
//    private ToAccountProxy toAccountProxy;


    @Autowired
    private AccountRepository accountRepository;



    @Transactional(rollbackFor = Exception.class)
    public void tryUpdate(Long id,Long transMoney,Long toId){


        if (accountRepository.updateBalance(id,-transMoney) <= 0) {
            throw new RuntimeException("扣减失败");
        }
//        toAccountProxy.account(toId,transMoney);

        log.info("扣钱");

    }

}
