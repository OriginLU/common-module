package com.charleslu.design.pattern.state;

import com.zeroone.design.patterns.state.Activity;
import com.zeroone.design.patterns.state.StateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeActivity extends Activity<String,String> {


    @Autowired
    public TradeActivity(List<StateAction<String, String>> stateActions) {
        super(stateActions);
    }

    @Override
    public String getStateDesc() {
        return StateEnum.fromType(getState()).getDesc();
    }
}
