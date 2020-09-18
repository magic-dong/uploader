package com.lzd.upload.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPoolConfig;

/**
 * redis缓存配置类
 * @author lzd
 * @date 2019年6月19日
 * @version
 */
@Configuration
@PropertySource("classpath:config/redis.properties")
public class RedisConfig {

	@Autowired
	private RedisProperties redisProperties;
	
	 /**
     * Redis配置连接池
     * @author lzd
     * @date 2019年7月16日:上午10:54:47
     * @return
     * @description
     */
    @Bean(name= "spring.redis.pool")
    public JedisPoolConfig jedisPoolConfig () {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲数
        jedisPoolConfig.setMaxIdle(redisProperties.maxIdle);
        // 连接池的最大数据库连接数
        jedisPoolConfig.setMaxTotal(redisProperties.maxTotal);
        // 最大建立连接等待时间
        jedisPoolConfig.setMaxWaitMillis(redisProperties.maxWaitMillis);
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.setMinEvictableIdleTimeMillis(redisProperties.minEvictableIdleTimeMillis);
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        jedisPoolConfig.setNumTestsPerEvictionRun(redisProperties.numTestsPerEvictionRun);
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(redisProperties.timeBetweenEvictionRunsMillis);
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        jedisPoolConfig.setTestOnBorrow(redisProperties.testOnBorrow);
        // 在空闲时检查有效性, 默认false
        jedisPoolConfig.setTestWhileIdle(redisProperties.testWhileIdle);
        return jedisPoolConfig;
    }
    
	/**
	 * Redis配置工厂
	 * @author lzd
	 * @date 2019年7月16日:上午10:54:25
	 * @param jedisPoolConfig
	 * @return
	 * @description
	 */
    @Bean(name= "spring.redis.factory")
    public JedisConnectionFactory JedisConnectionFactory(@Qualifier("spring.redis.pool")JedisPoolConfig jedisPoolConfig){
        JedisConnectionFactory JedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        //IP地址  
        JedisConnectionFactory.setHostName(redisProperties.host);  
        //端口号  
        JedisConnectionFactory.setPort(redisProperties.port);  
        //Redis密码  
        JedisConnectionFactory.setPassword(redisProperties.password);  
        //客户端超时时间单位是毫秒  
        JedisConnectionFactory.setTimeout(redisProperties.timeout);
        //连接池  
        JedisConnectionFactory.setPoolConfig(jedisPoolConfig);  
        return JedisConnectionFactory; 
    }
    

    /**
     * 实例化RedisTemplate对象,设置序列化方式，并开启事务
     * @author lzd
     * @date 2019年7月16日:上午10:26:54
     * @param redisTemplate
     * @param factory
     * @description
     */
    @Bean
    public RedisTemplate<String, Object> initRedisTemplate(@Qualifier("spring.redis.factory")RedisConnectionFactory factory) {
    	RedisTemplate<String, Object> redisTemplate=new RedisTemplate<>();
        //如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast to String！  
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 开启事务
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }
    
    /**
     * Redis属性配置
     * @author lzd
     * @date 2019年7月16日
     * @version
     */
    @Component
	@ConfigurationProperties(prefix = "spring.redis")
	class RedisProperties{
    	//IP地址  
    	private String host;
    	//端口号  
    	private Integer port;
    	//Redis密码  
    	private String password;
    	//客户端超时时间单位是毫秒  
    	private Integer timeout;
    	// 最大空闲数
        private Integer maxIdle;
        // 连接池的最大数据库连接数
        private Integer maxTotal;
        // 最大建立连接等待时间
        private Integer maxWaitMillis;
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        private Integer minEvictableIdleTimeMillis;
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        private Integer numTestsPerEvictionRun;
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        private long timeBetweenEvictionRunsMillis;
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        private boolean testOnBorrow;
        // 在空闲时检查有效性, 默认false
        private boolean testWhileIdle;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Integer getTimeout() {
			return timeout;
		}

		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}

		public Integer getMaxIdle() {
			return maxIdle;
		}

		public void setMaxIdle(Integer maxIdle) {
			this.maxIdle = maxIdle;
		}

		public Integer getMaxTotal() {
			return maxTotal;
		}

		public void setMaxTotal(Integer maxTotal) {
			this.maxTotal = maxTotal;
		}

		public Integer getMaxWaitMillis() {
			return maxWaitMillis;
		}

		public void setMaxWaitMillis(Integer maxWaitMillis) {
			this.maxWaitMillis = maxWaitMillis;
		}

		public Integer getMinEvictableIdleTimeMillis() {
			return minEvictableIdleTimeMillis;
		}

		public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
			this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
		}

		public Integer getNumTestsPerEvictionRun() {
			return numTestsPerEvictionRun;
		}

		public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) {
			this.numTestsPerEvictionRun = numTestsPerEvictionRun;
		}

		public long getTimeBetweenEvictionRunsMillis() {
			return timeBetweenEvictionRunsMillis;
		}

		public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
			this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
		}

		public boolean isTestOnBorrow() {
			return testOnBorrow;
		}

		public void setTestOnBorrow(boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		public boolean isTestWhileIdle() {
			return testWhileIdle;
		}

		public void setTestWhileIdle(boolean testWhileIdle) {
			this.testWhileIdle = testWhileIdle;
		}
    }
    
}