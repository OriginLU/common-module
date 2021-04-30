package com.zeroon.rocketmq.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangeEvent implements Serializable {
    /**
     * 账号
     */
    private Long accountNo;
    /**
     * 变动金额
     */
    private Long amount;
    /**
     * 事务号
     */
    private String txNo;
 
}
