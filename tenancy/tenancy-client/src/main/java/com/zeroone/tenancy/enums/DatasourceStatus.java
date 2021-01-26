package com.zeroone.tenancy.enums;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum DatasourceStatus {

    INIT(1,"就绪"),
    CREATE(2,"创建"),
    OVERRIDE(3,"重新创建"),
    RUNNING(4,"运行中"),
    REMOVE(5,"移除"),
    ERROR_INPUT(-1,"错误状态"),

    ;


    private final int status;

    private final String desc;

    private static Map<Integer, DatasourceStatus> valueMap;

    static {
        valueMap = new HashMap<>(DatasourceStatus.values().length);
        for (DatasourceStatus type : DatasourceStatus.values()) {
            valueMap.put(type.getStatus(), type);
        }
    }

    public static DatasourceStatus fromType(int code) {
        return Optional.ofNullable(valueMap.get(code)).orElse(DatasourceStatus.ERROR_INPUT);
    }

    DatasourceStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
