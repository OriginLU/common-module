package com.charleslu.design.patterns.state;

/**
 *
 */
public interface StateAction<I,O>{

    void doAction(I in,O out,Activity<I,O> activity);
}
