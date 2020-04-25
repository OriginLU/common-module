package com.zeroone.design.patterns.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStateAction<I,O> implements StateAction<I,O> {


    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doAction(I in, O out, Activity<I, O> activity) {
        log.info("[{}处理器]入参:{}",activity.getStateDesc(),in);
        executeAction(in,out,activity);
    }

    protected abstract void executeAction(I in, O out, Activity<I, O> activity);
}
