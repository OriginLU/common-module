package com.charleslu.design.patterns.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Activity<I,O> {


    private List<StateAction<I,O>> actions;

    private  Map<Integer,StateAction<I,O>> stateActionMap = new HashMap<>();


    public Activity(List<StateAction<I, O>> actions) {
        this.actions = actions;
        this.init();
    }

    private void init(){
        actions.forEach(act -> Optional.ofNullable(act.getClass().getDeclaredAnnotation(State.class))
                    .ifPresent(state -> stateActionMap.put(state.value(),act)));
    }


    public void doAction(int state,I in,O out){
        stateAction(state).doAction(in,out,this);
    }

    private StateAction<I,O> stateAction(int state){
        return stateActionMap.get(state);
    }

}
