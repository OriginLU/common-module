package com.zeroone.tenancy.demo.service;

import com.zeroone.tenancy.demo.entity.OpenAccountRecord;
import org.springframework.stereotype.Service;

@Service
public class TestService {


    public OpenAccountRecord openAccountRecord(){
        return new OpenAccountRecord();
    }
}
