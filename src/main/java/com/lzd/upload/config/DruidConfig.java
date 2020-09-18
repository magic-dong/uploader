package com.lzd.upload.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * Druid配置多数据源
 * 
 * @author lzd
 * @date 2019年5月22日
 * @version
 */
@Configuration
public class DruidConfig {
	
	@Bean
	@ConfigurationProperties("spring.datasource.druid")
	public DataSource dataSource() {
		DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
		return dataSource;
	}
}
