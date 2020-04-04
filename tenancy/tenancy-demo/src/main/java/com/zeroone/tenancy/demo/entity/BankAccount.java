package com.zeroone.tenancy.demo.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "bank_account")
@Accessors(chain = true)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 对象id(根据账户类型推断)
     */
    @Column(name = "target_id")
    private String targetId;

    /**
     * 银行账户
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * 银行卡账号
     */
    @Column(name = "account_number")
    private String accountNumber;

    /**
     * 开户行地址
     */
    @Column(name = "open_account_bank_address")
    private String openAccountBankAddress;

    /**
     * 开户行名称
     */
    @Column(name = "open_account_bank_name")
    private String openAccountBankName;

    /**
     * 开户唯一码
     */
    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    /**
     * 渠道id
     */
    @Column(name = "channel_type")
    private Integer channelType;



    @Column(name = "delete_status")
    protected Boolean deleteStatus;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private ZonedDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "modify_time")
    private ZonedDateTime modifyTime;


    @PrePersist
    private void beforeSave() {
        ZonedDateTime now = ZonedDateTime.now();
        this.createTime = now;
        this.modifyTime = now;
        if (null == this.deleteStatus) {
            this.deleteStatus = Boolean.FALSE;
        }
    }

    @PreUpdate
    private void beforeUpdate() {
        this.modifyTime = ZonedDateTime.now();
        if (null == this.deleteStatus) {
            this.deleteStatus = Boolean.FALSE;
        }
    }


}
