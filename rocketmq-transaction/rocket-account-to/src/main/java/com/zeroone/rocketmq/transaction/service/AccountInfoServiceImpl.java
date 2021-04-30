package com.zeroone.rocketmq.transaction.service;

import com.zeroone.rocketmq.transaction.entity.AccountChangeEvent;
import com.zeroone.rocketmq.transaction.entity.LocalTryLog;
import com.zeroone.rocketmq.transaction.repository.AccountRepository;
import com.zeroone.rocketmq.transaction.repository.LocalTryLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
 
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private LocalTryLogRepository localTryLogRepository;
 
    //更新账户，增加金额
    @Override
    @Transactional
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent) {
        log.info("bank2更新本地账号，账号：{},金额：{}",accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
        if(localTryLogRepository.existsByTxNumber(accountChangeEvent.getTxNo())){
            return ;
        }
        //增加金额
        accountRepository.updateBalance(accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
        //添加事务记录，用于幂等
        localTryLogRepository.save(LocalTryLog.build(accountChangeEvent.getTxNo()));
        if(accountChangeEvent.getAmount() == 4){
            throw new RuntimeException("人为制造异常");
        }
    }
}
