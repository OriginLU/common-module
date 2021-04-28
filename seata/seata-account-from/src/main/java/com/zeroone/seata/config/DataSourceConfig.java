package com.zeroone.seata.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {



	/**
	 * need to DataSourceProxy as the primary data source, otherwise the transaction
	 * can not be rolled back
	 */
	@Primary
	@Bean("dataSource")
	public DataSource dataSource(HikariDataSource hikariDataSource) {
		return new DataSourceProxy(hikariDataSource);
	}
}