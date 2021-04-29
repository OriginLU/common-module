package com.zeroone.seata.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	HikariDataSource dataSource(DataSourceProperties properties) {
		HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
		if (StringUtils.hasText(properties.getName())) {
			dataSource.setPoolName(properties.getName());
		}
		return dataSource;
	}

	protected static <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
		return (T) properties.initializeDataSourceBuilder().type(type).build();
	}

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