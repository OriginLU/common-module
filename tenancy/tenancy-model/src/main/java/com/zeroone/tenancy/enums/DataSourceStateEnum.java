package com.zeroone.tenancy.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源活动状态
 * @author Charles
 * @since 2020-04-03
 */
public enum DataSourceStateEnum {


    CREATE(0,"创建"),
    ACTIVE(1,"活动"),
    DESTROY(2,"销毁");


    private int code;

    private String desc;


    private static Map<Integer,DataSourceStateEnum> valueMap = new HashMap<>() ;

    static {

        for (DataSourceStateEnum value : DataSourceStateEnum.values()) {
            valueMap.put(value.getCode(),value);
        }
    }

    DataSourceStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DataSourceStateEnum fromCode(int code){
        return valueMap.get(code);
    }
}
