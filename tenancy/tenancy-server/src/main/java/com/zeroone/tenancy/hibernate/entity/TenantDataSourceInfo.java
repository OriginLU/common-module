package com.zeroone.tenancy.hibernate.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zero-one.lu
 * @since 2020-04-03
 */
@Data
@Accessors(chain = true)
@ToString
@Entity
@Table(name = "t_tenant_data_source_info")
public class TenantDataSourceInfo implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tenant_code")
    private String tenantCode;
    /**
     * 数据库链接 url
     */
    @Column(name = "url")
    private String url;

    /**
     * 数据库
     */
    @Column(name = "database")
    private String database;
    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;
    /**
     * 密码
     */
    @Column(name = "password")
    private String password;
    /**
     * 服务名
     */
    @Column(name = "server_name")
    private String serverName;
    /**
     * 数据库类型 mongo/mysql
     */
    @Column(name = "type")
    private String type;


    @Column(name = "require_override")
    private Integer requireOverride;

    /**
     * 数据源状态
     */
    @Column(name = "state")
    private Integer state;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "modify_time")
    private Date modifyTime;
}
