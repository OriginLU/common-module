package com.zeroone.tenancy.demo;


import com.zeroone.tenancy.demo.entity.BankAccount;
import com.zeroone.tenancy.demo.repository.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JpaTest {



    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    public void repositoryTest(){

        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(1L);

        optionalBankAccount.ifPresent(bankAccount -> log.info(bankAccount.toString()));
    }
}
