package com.charleslu.design.pattern.state;

import com.zeroone.design.patterns.state.Activity;
import com.zeroone.design.patterns.state.StateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoneActivity extends Activity<Object,Object> {

    @Autowired
    public NoneActivity(List<StateAction<Object, Object>> stateActions) {
        super(stateActions);
    }

    @Override
    public String getStateDesc() {
        return "none";
    }
}
