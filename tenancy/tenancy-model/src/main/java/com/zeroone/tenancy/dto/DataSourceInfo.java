package com.zeroone.tenancy.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Properties;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceInfo {


    private String tenantCode;
    /**
     * 数据库链接 url
     */
    private String url;

    /**
     * 数据库
     */
    private String database;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 服务名
     */
    private String serverName;
    /**
     * 数据库类型 mongo/mysql
     */
    private String type;


    private Boolean requireOverride = true;

    /**
     * 连接配置信息
     */
    private Properties properties;
}
