package com.zeroone.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author zero-one.lu
 * @since 2021-04-11
 */
@Service
public class LazyService {



    @Autowired
    @Lazy
    private TestService testService;


    public void say(){
        System.out.println("hello ");
    }


    public void say0(){
        testService.say();
    }
}
