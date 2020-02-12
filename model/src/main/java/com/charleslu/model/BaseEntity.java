package com.charleslu.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Charles
 * @since 2020-02-02
 */
@Data
@ToString
@Accessors(chain = true)
public class BaseEntity implements Serializable {

    /**
     * 主键ID
     */
    protected Long id;

    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 修改时间
     */
    protected Date modifyTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建用户id
     */
    private Long createUserId;
    /**
     * 修改用户
     */
    private String modifyUser;

    /**
     * 修改用户Id
     */
    private Long modifyUserId;

}
