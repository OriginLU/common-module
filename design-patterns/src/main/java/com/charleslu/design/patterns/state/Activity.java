package com.charleslu.design.patterns.state;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class Activity<I,O> {


    private List<StateAction<I,O>> actions;

    private  Map<Integer,StateAction<I,O>> stateActionMap = new HashMap<>();


    public Activity(List<StateAction<I, O>> actions) {
        this.actions = actions;
        this.init();
    }

    private void init(){
        if (!CollectionUtils.isEmpty(actions)){
            actions.forEach(act -> Optional.ofNullable(act.getClass().getDeclaredAnnotation(State.class))
                    .ifPresent(state -> stateActionMap.put(state.value(),act)));
        }
    }


    public void doAction(int state,I in,O out){
        stateAction(state).doAction(in,out,this);
    }

    private StateAction<I,O> stateAction(int state){
        return Optional.ofNullable(stateActionMap.get(state)).orElse((in, out, activity) -> {
           log.info("未获取到状态处理器,当前状态为:" + state);
        });
    }

}
