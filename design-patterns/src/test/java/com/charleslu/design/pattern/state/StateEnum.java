package com.charleslu.design.pattern.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum StateEnum {

    /**
     * 0-未处理
     * 1-处理中
     */
    UN_PROCESSED(0,"未处理"),
    PROCESSING(1,"处理中"),
    SUCCESS(2,"处理成功"),
    FAIL(3,"处理失败"),
    TIMEOUT(4,"处理超时"),
    ERROR_INPUT(-1,"错误状态");

    /**
     * 状态码
     */
    private int code;

    /**
     * 描述
     */
    private String desc;


    private static Map<Integer, StateEnum> valueMap;

    static {
        valueMap = new HashMap<>(StateEnum.values().length);
        for (StateEnum type : StateEnum.values()) {
            valueMap.put(type.getCode(), type);
        }
    }

    public static StateEnum fromType(int code) {
        return Optional.ofNullable(valueMap.get(code)).orElse(StateEnum.ERROR_INPUT);
    }

    StateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
