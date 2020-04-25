package com.charleslu.design.pattern.state;

import com.zeroone.design.patterns.state.Activity;
import com.zeroone.design.patterns.state.State;
import com.zeroone.design.patterns.state.StateAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@State(0)
public class UnprocessedState implements StateAction<String,String> {


    @Override
    public void doAction(String in, String out, Activity<String, String> activity) {

        log.info("处理流程:{}",StateEnum.UN_PROCESSED.getDesc());
        activity.doAction(StateEnum.PROCESSING.getCode(),in,out);
    }
}
