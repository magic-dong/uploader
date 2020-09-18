package com.lzd.upload.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 获取Spring托管中的对象
 * @author lzd
 * @date 2019年8月28日
 * @version
 */
@Component
@Lazy(false)
public class SpringContextUtils implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;
	 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	if(SpringContextUtils.applicationContext==null){
    		SpringContextUtils.applicationContext = applicationContext;
    	}
    }
 
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    public static <T> T getBean(Class<T> cls) {
        if (applicationContext == null) {
            throw new RuntimeException("applicationContext注入失败");
        }
        return getApplicationContext().getBean(cls);
    }
 
    public static Object getBean(String name) {
        if (applicationContext == null) {
            throw new RuntimeException("applicationContext注入失败");
        }
        return getApplicationContext().getBean(name);
    }
 
    public static <T> T getBean(String name, Class<T> cls) {
        if (applicationContext == null) {
            throw new RuntimeException("applicationContext注入失败");
        }
        return getApplicationContext().getBean(name, cls);
    }
 
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
        return requestAttributes == null ? null : requestAttributes.getRequest();
    }

}
