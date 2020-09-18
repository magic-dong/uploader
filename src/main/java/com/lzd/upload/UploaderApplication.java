package com.lzd.upload;

import java.io.IOException;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动路径
 * @author lzd
 * @date 2019年3月29日
 * @version
 */
@EnableScheduling
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class UploaderApplication{
	
	/**
	 * 解决注解@PropertySource不能写通配符
	 * @author lzd
	 * @date 2019年8月15日:下午2:16:00
	 * @return
	 * @throws IOException
	 * @description
	 */
	@Bean
	public PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() throws IOException{
		PropertyPlaceholderConfigurer config = new PropertyPlaceholderConfigurer();
		config.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath:config/**/*.properties"));
		config.setIgnoreUnresolvablePlaceholders(true);
	    return config;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(UploaderApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  文件上传项目启动成功   ლ(´ڡ`ლ)ﾞ");
	}
	
}
