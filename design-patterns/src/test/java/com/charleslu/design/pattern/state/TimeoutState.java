package com.charleslu.design.pattern.state;

import com.charleslu.design.patterns.state.Activity;
import com.charleslu.design.patterns.state.State;
import com.charleslu.design.patterns.state.StateAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@State(4)
public class TimeoutState  implements StateAction<String,String> {
    @Override
    public void doAction(String in, String out, Activity<String, String> activity) {
        log.info("处理流程:{}",StateEnum.TIMEOUT.getDesc());
        log.info("处理流程结束");
    }
}
