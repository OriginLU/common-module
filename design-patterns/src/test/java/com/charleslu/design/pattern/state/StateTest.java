package com.charleslu.design.pattern.state;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StateTest {



    @Autowired
    private TradeActivity activity;

    @Test
    public void stateTest(){

        activity.doAction(StateEnum.UN_PROCESSED.getCode(),"in","out");
    }
}
