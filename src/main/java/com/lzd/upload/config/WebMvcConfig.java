package com.lzd.upload.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Web配置
 * 
 * @author lzd
 * @date 2019年4月2日
 * @version
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	/**
	 * 跨域支持
	 * 
	 * @return
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				 		.allowCredentials(true)  
				 		.allowedHeaders("*")  
				 		.allowedOrigins("*")  
					    .allowedMethods("*");
			}
		};
	}

	/**
	 * 设置 http:127.0.0.1:8080默认访问页
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addViewController("/").setViewName("redirect:index.html");
		//设置自定义页面访问优先（不设置会优先访问默认的index.html，Order值越低优先级越高）
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		// 解决中文乱码问题
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(responseBodyConverter());
		super.configureMessageConverters(converters);
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 文件上传路径 */
        registry.addResourceHandler("/profile/**").addResourceLocations("file:" + Global.getProfile());
    }
}
