package com.zeroone.tenancy.enums;


public enum DatasourceAction {
    INIT(1,"初始化"),
    CREATE(2,"创建"),
    USING(3,"使用"),
    REMOVE(4,"移除"),

    ;


    private Integer action;

    private String desc;


    DatasourceAction(Integer action, String desc) {
        this.action = action;
        this.desc = desc;
    }

    public Integer getAction() {
        return action;
    }

    public String getDesc() {
        return desc;
    }
}
