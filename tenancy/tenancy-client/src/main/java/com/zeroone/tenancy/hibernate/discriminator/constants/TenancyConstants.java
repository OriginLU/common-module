package com.zeroone.tenancy.hibernate.discriminator.constants;

import org.hibernate.type.LongType;
import org.hibernate.type.Type;

public interface TenancyConstants {

   /**
    * 过滤器名称
    */
   String TENANT_FILTER = "TENANT_FILTER";

   /**
    * 字段名称
    */
   String TENANT_COLUMN = "mch_id";

   /**
    * 条件
    */
   String CONDITION = "mch_id = :mch_id";

   /**
    * 类型
    */
   String TYPE = "long";

   /**
    * 字段类型
    */
   Type COLUMN_TYPE = new LongType();
}
