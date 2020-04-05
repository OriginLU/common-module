package com.zeroone.tenancy.demo.controller;

import com.zeroone.tenancy.demo.entity.BankAccount;
import com.zeroone.tenancy.demo.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.PublicKey;
import java.util.Optional;

@ResponseBody
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BankAccountRepository bankAccountRepository;


    @GetMapping("/testApi")
    public String testApi(){

        return "testApi";
    }


     @GetMapping("find/{id}")
     public BankAccount getBankAccount(@PathVariable Long id){
         return bankAccountRepository.findById(id).orElse(null);
     }
}
