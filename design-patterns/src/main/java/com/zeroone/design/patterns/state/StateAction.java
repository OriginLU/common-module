package com.zeroone.design.patterns.state;

/**
 * 状态执行器
 */
public interface StateAction<I,O>{

    void doAction(I in,O out,Activity<I,O> activity);
}
