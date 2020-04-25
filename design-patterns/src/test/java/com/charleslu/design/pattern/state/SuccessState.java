package com.charleslu.design.pattern.state;

import com.zeroone.design.patterns.state.Activity;
import com.zeroone.design.patterns.state.State;
import com.zeroone.design.patterns.state.StateAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@State(2)
public class SuccessState  implements StateAction<String,String> {
    @Override
    public void doAction(String in, String out, Activity<String, String> activity) {
        log.info("处理流程:{}",StateEnum.SUCCESS.getDesc());
        log.info("处理流程结束");
    }
}
