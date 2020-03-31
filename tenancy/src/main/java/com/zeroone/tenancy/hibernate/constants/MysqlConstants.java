package com.zeroone.tenancy.hibernate.constants;

public interface MysqlConstants {


    String QUERY_SCHEMA_SQL = "select count(sc.SCHEMA_NAME) from information_schema.SCHEMATA sc where sc.SCHEMA_NAME = ? ";

    String CREATE_DATABASE_SQL = "create database `%s` default character set %s";

    String USE_DATABASE_SQL = "use database `%s`";

    String DEFAULT_CHARSET = " utf8mb4";
}
