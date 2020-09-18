package com.lzd.upload.config;

import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 配置定时任务器使用多线程线程池
 * 默认是使用一个单线程，当同一刻执行多个任务时，
 * 需要等待最先的任务执行完，才能继续执行任务
 * @author lzd
 * @date 2019年11月21日
 * @version
 */
@Configuration
public class SchedulingConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
		taskRegistrar.setScheduler(Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()));
	}

}
