package com.zeroone.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.type.StringType;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@Data
@Table(name = "xa01")
@Entity
@FilterDef(name = "TENANT_FILTER",defaultCondition = "merchant_id = :merchant_id",parameters = @ParamDef(name = "merchant_id",type = "string"))
@Filter(name = "TENANT_FILTER")
public class Xa01 implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String title;

    private String text;

    private String merchantId;
}
