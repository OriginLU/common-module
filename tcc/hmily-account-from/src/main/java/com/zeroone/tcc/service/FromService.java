package com.zeroone.tcc.service;

import com.zeroone.tcc.entity.LocalCancelLog;
import com.zeroone.tcc.entity.LocalTryLog;
import com.zeroone.tcc.proxy.ToAccountProxy;
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
public class FromService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ToAccountProxy toAccountProxy;


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
    public void tryUpdate(Long id,Long transMoney,Long toId){

        //事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("******** Bank1 Service  begin try {}  ",transId );
        //try幂等校验
        if(localTryLogRepository.existsByTxNumber(transId)){
            log.info("******** Bank1 Service 已经执行try，无需重复执行，事务id:{}",transId );
            return ;
        }
        //try悬挂处理
        if(localCancelLogRepository.existsByTxNumber(transId) || localConfirmLogRepository.existsByTxNumber(transId)){
            log.info("******** Bank1 Service 已经执行confirm或cancel，悬挂处理，事务id:{} ",transId );
            return ;
        }
        if (accountRepository.updateBalance(id,-transMoney) <= 0) {
            throw new RuntimeException("扣减失败");
        }
        localTryLogRepository.save(LocalTryLog.build(transId));
        toAccountProxy.account(toId,transMoney);

        log.info("扣钱");

    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmUpdate(Long id,Long transMoney,Long toId){
        String localTradeNo = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("扣钱确认:{}",localTradeNo);
    }


    @Transactional(rollbackFor = Exception.class)
    public void cancelUpdate(Long id,Long transMoney,Long toId){

        String localTradeNo = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("******** from service begin rollback... {} " ,localTradeNo);
        //空回滚处理，try阶段没有执行什么也不用做
        if(!localTryLogRepository.existsByTxNumber(localTradeNo)){
            log.info("******** try阶段失败... 无需rollback:{} ",localTradeNo );
            return;
        }
        //幂等性校验，已经执行过了，什么也不用做
        if(localCancelLogRepository.existsByTxNumber(localTradeNo)){
            log.info("******** 已经执行过rollback... 无需再次rollback:{} ",localTradeNo);
            return;
        }
        //再将金额加回账户
        if (accountRepository.updateBalance(id,transMoney) <= 0){
            throw new RuntimeException("回滚失败");
        }
        //添加cancel日志，用于幂等性控制标识
        localCancelLogRepository.save(LocalCancelLog.build(localTradeNo));

        log.info("取消扣钱");
    }
}
