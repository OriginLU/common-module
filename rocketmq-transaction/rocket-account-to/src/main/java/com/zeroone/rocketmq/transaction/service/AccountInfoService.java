package com.zeroone.rocketmq.transaction.service;


import com.zeroone.rocketmq.transaction.entity.AccountChangeEvent;

/**
 * Created by Administrator.
 */
public interface AccountInfoService {
 
    //向mq发送转账消息
     void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);

 
}
