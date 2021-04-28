package com.zeroone.tcc.service;

import com.zeroone.tcc.entity.LocalConfirmLog;
import com.zeroone.tcc.repository.AccountRepository;
import com.zeroone.tcc.repository.LocalCancelLogRepository;
import com.zeroone.tcc.repository.LocalConfirmLogRepository;
import com.zeroone.tcc.repository.LocalTryLogRepository;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
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

    @Autowired
    private LocalTryLogRepository localTryLogRepository;

    @Autowired
    private LocalConfirmLogRepository localConfirmLogRepository;

    @Autowired
    private LocalCancelLogRepository localCancelLogRepository;


    @Transactional(rollbackFor = Exception.class)
    @Hmily(confirmMethod = "confirmUpdate",cancelMethod = "cancelUpdate")
    public void tryUpdate(Long id,Long transMoney){
        String localTradeNo = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("加钱准备:{}",localTradeNo);
    }


    @Transactional(rollbackFor = Exception.class)
    public void confirmUpdate(Long id,Long transMoney){

        String localTradeNo = HmilyTransactionContextLocal.getInstance().get().getTransId();
        //幂等性校验，已经执行过了，什么也不用做
        if(localConfirmLogRepository.existsByTxNumber(localTradeNo)){
            log.info("******** 已经执行过confirm... 无需再次confirm :{}",localTradeNo );
            return ;
        }
        if (accountRepository.updateBalance(id,transMoney) <= 0) {
            throw new RuntimeException("加钱失败");
        }

        localConfirmLogRepository.save(LocalConfirmLog.build(localTradeNo));

        log.info("加钱确认");
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelUpdate(Long id,Long transMoney){
        String localTradeNo = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("加钱取消:{}",localTradeNo);
    }
}
