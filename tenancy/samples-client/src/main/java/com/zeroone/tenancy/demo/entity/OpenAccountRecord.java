package com.zeroone.tenancy.demo.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * <p>Title: 开户记录表 </p>  
 * <p>Description: 用于记录开户记录</p>  
 */
@Data
@Generated
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "open_account_record")
public class OpenAccountRecord {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 请求流水号
     */
    @Column(name = "req_serial_number")
    private String reqSerialNumber;


    /**
     * 唯一码
     */
    @Column(name = "unique_code")
    private Long uniqueCode;

    /**
     * 协议号
     */
    @Column(name = "protocol_number")
    private String protocolNumber;
    /**
     * 账户名称
     */
    @Column(name = "account_name")
    private String accountName;

    /**
     * 银行卡号
     */
    @Column(name = "account_number")
    private String accountNumber;

    /**
     * 账户银行名称
     */
    @Column(name = "account_bank_name")
    private String accountBankName;


    /**
     * 交易会员性质
     * 0-企业 1-个人
     */
    @Column(name = "customer_type")
    private Integer customerType;

    /**
     * 联系人
     */
    @Column(name = "contact")
    private String contact;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone")
    private String contactPhone;
    /**
     * 手机号码
     */
    @Column(name = "tel_phone")
    private String telPhone;
    /**
     * 联系地址
     */
    @Column(name = "contact_addr")
    private String contactAddr;
    /**
     * 法人姓名
     */
    @Column(name = "business_name")
    private String businessName;
    /**
     * 证件类型
     */
    @Column(name = "credential_type")
    private String credentialType;

    /**
     * 证件代码
     */
    @Column(name = "credential_code")
    private String credentialCode;

    /**
     * 邮件地址
     */
    @Column(name = "email")
    private String email;

    /**
     * 支付机构代码
     */
    @Column(name = "pay_institution_type")
    private Long payInstitutionType;

    /**
     * 通道类型
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 通道类型
     */
    @Column(name = "channel_code")
    private Long channelCode;

    /**
     * 开户状态 @see {@link com.taoqicar.channel.enums.OpenAccountStatusEnum}
     */
    @Column(name = "open_account_status")
    private Integer openAccountStatus;

    /**
     * 签约返回信息
     */
    @Column(name = "message")
    private String message;


    /**
     * 共享支付通道.表示这条用户数据是来自某个支付通道的共享
     */
    @Column(name = "sharing_from")
    private Long sharingFrom;


//    /**
//     * 开户来源 @see {@link com.taoqicar.pay.enums.OpenSourceEnum}
//     */
//    @Column(name = "order_source")
//    private Integer openSource;



    /**
     * 操作用户
     */
    @Column(name = "operation_user")
    private String operationUser;

    /**
     * 操作用户ID
     */
    @Column(name = "operation_id")
    private Long operationId;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;


    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private ZonedDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "modify_time")
    private ZonedDateTime modifyTime;

    /**
     * 是否删除
     */
    @Column(name = "delete_status")
    private Integer deleteStatus;



}
