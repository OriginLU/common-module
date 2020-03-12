package com.charleslu.design.pattern.state;

import com.charleslu.design.patterns.state.Activity;
import com.charleslu.design.patterns.state.State;
import com.charleslu.design.patterns.state.StateAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@State(1)
public class ProcessingState  implements StateAction<String,String> {
    @Override
    public void doAction(String in, String out, Activity<String, String> activity) {
        log.info("处理流程:{}",StateEnum.PROCESSING.getDesc());
        activity.doAction(new Random().nextInt(2) + 2,in,out);
    }
}
