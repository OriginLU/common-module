package com.zeroone.tenancy.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.Properties;

@Data
@ToString
@Accessors(chain = true)
public class DataSourceInfo {


    @NotBlank(message = "租户号不能为空白")
    private String tenantCode;
    /**
     * 数据库链接 url
     */
    @NotBlank(message = "数据库链接不能为空")
    private String url;

    /**
     * 数据库
     */
    @NotBlank(message = "数据库不能为空")
    private String database;
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 服务名
     */
    @NotBlank(message = "服务名不能为空")
    private String serverName;

    /**
     * 数据库类型 mongo/mysql
     */
    @NotBlank(message = "数据库类型不能为空")
    private String type;


    private Boolean requireOverride = true;

    /**
     * 连接配置信息
     */
    private Properties properties;
}
