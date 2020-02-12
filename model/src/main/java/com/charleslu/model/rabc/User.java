package com.charleslu.model.rabc;

import com.charleslu.model.BaseEntity;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Charles
 * @since 2020-02-02
 */
@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class User extends BaseEntity {


    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;


}
