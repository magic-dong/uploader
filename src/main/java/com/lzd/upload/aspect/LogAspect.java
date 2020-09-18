package com.lzd.upload.aspect;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lzd.upload.utils.IpUtils;

/**
 * 日志切面（记录访问者请求路径，ip地址，请求方法，请求参数）
 * 注意：切面里面使用@Order()指定优先级的时候，值越小，优先级越高。
 * 但不能比ExposeInvocationInterceptor类（默认为Ordered.HIGHEST_PRECEDENCE+1）
 * 优先级还要高，此类作用于方法等拦截；
 * 否则抛异常java.lang.IllegalStateException: No MethodInvocation found.....
 * @author lzd
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+2)
public class LogAspect {
	 	private static final Logger logger= LoggerFactory.getLogger(LogAspect.class);

	    @Pointcut("(execution(* com.lzd.upload.controller..**.*(..)))")
	    public void log(){ }

	    @Before("log()")
	    public void doBefore(JoinPoint joinPoint){
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        HttpServletRequest request = attributes.getRequest();
  	        String url = request.getRequestURL().toString();
  	        String ip =IpUtils.getIpAddr(request);
  	        String classMethod =joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
  	        Object[] args = joinPoint.getArgs();
  	        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
  	        logger.info("Request : {}", requestLog);
	    }
	    
	    @After("log()")
	    public void doAfter(){
	        //logger.info("------doAfter--------");
	    }

	    
	    @AfterReturning(returning = "result",pointcut = "log()")
	    public void doAfterRuturn(Object result){
	        logger.info("Result : {}", result);
	    }

	    private static class RequestLog {
	        //请求路径
	        private String url;
	        //请求ip地址
	        private String ip;
	        //请求方法
	        private String classMethod;
	        //请求参数
	        private Object[] args;

	        public RequestLog(String url, String ip, String classMethod, Object[] args) {
	            this.url = url;
	            this.ip = ip;
	            this.classMethod = classMethod;
	            this.args = args;
	        }

	        @Override
	        public String toString() {
	            return "{" +
	                    "url='" + url + '\'' +
	                    ", ip='" + ip + '\'' +
	                    ", classMethod='" + classMethod + '\'' +
	                    ", args=" + Arrays.toString(args) +
	                    '}';
	        }
	    }
}
