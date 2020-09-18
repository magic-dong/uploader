package com.lzd.upload.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * 线程池配置、启用异步
 * @author lzd
 * @date 2019年6月19日
 * @version
 */
@EnableAsync(proxyTargetClass = true)
@Configuration
public class AsyncTaskExecutorConfig {
	
	@Autowired
	private AsyncTaskExecutorProperties  asyncProperties;
	
	@Bean
	public  MyThreadPoolTaskExecutor asyncServiceExecutor(){
		MyThreadPoolTaskExecutor executor = new MyThreadPoolTaskExecutor();
		// 配置核心线程数
		executor.setCorePoolSize(asyncProperties.getCorePoolSize());
		// 配置最大线程数
		executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
		// 配置队列大小
		executor.setQueueCapacity(asyncProperties.getQueueCapacity());
		// 允许线程的空闲时间(单位：秒)
		executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
		// 配置线程池中的线程的名称前缀
		executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
		// rejection-policy：当pool已经达到max size的时候，如何处理新任务
		// CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 执行初始化
		executor.initialize();
		return executor;
	}
	
	@Component
	@ConfigurationProperties(prefix = "asyncTask.executor")
	class AsyncTaskExecutorProperties{
		// 核心线程数
		private int corePoolSize;
		// 最大线程数
		private int maxPoolSize;
		// 队列大小
		private int queueCapacity;
		// 允许线程的空闲时间(单位：秒)
		private int keepAliveSeconds;
		// 线程的名称前缀
		private String threadNamePrefix;

		public int getCorePoolSize() {
			return corePoolSize;
		}

		public void setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
		}

		public int getMaxPoolSize() {
			return maxPoolSize;
		}

		public void setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		public int getQueueCapacity() {
			return queueCapacity;
		}

		public void setQueueCapacity(int queueCapacity) {
			this.queueCapacity = queueCapacity;
		}

		public int getKeepAliveSeconds() {
			return keepAliveSeconds;
		}

		public void setKeepAliveSeconds(int keepAliveSeconds) {
			this.keepAliveSeconds = keepAliveSeconds;
		}

		public String getThreadNamePrefix() {
			return threadNamePrefix;
		}

		public void setThreadNamePrefix(String threadNamePrefix) {
			this.threadNamePrefix = threadNamePrefix;
		}
	}
}
