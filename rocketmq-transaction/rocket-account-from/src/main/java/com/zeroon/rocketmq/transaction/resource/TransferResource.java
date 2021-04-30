package com.zeroon.rocketmq.transaction.resource;

import com.zeroon.rocketmq.transaction.entity.AccountChangeEvent;
import com.zeroon.rocketmq.transaction.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransferResource {


    @Autowired
    private AccountInfoService accountInfoService;

    @GetMapping("/api/transfer/{account}/{amount}")
    public String transfer(@PathVariable("account") Long account, @PathVariable("amount") Long amount) {
        AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
        accountChangeEvent.setAccountNo(account);
        accountChangeEvent.setAmount(amount);
        accountChangeEvent.setTxNo(String.valueOf(System.nanoTime()));
        accountInfoService.sendUpdateAccountBalance(accountChangeEvent);
        return "true";
    }
}
