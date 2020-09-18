package com.lzd.upload.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

/**
 * Redis工具类
 * 
 * @author lzd
 * @date 2019年7月16日
 * @version
 */
@Component
public class RedisUtils {

	private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
	
	private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    
	private static RedisTemplate<String, Object> redisTemplate;

	@Autowired 
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisUtils.redisTemplate = redisTemplate;
	}

	// =============================common============================

	/**
	 * 指定缓存失效时间
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:23:39
	 * @param key
	 *            键
	 * @param time
	 *            时间(秒)
	 * @return
	 * @description
	 */
	public static boolean expire(String key, long time) {
		try {
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	/**
	 * 根据key 获取过期时间
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:29:06
	 * @param key
	 *            键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 * @description
	 */
	public static long getExpire(String key) {
		try {
			Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
			return expire;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 判断key是否存在
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:31:57
	 * @param key
	 *            键
	 * @return true:存在 false:不存在
	 * @description
	 */
	public static boolean hasKey(String key) {

		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 删除缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:33:47
	 * @param key
	 *            可以传一个值 或多个
	 * @description
	 */
	@SuppressWarnings("unchecked")
	public static boolean del(String... key) {
		try {
			if (key != null && key.length > 0) {
				if (key.length == 1) {
					redisTemplate.delete(key[0]);
				} else {
					redisTemplate.delete(CollectionUtils.arrayToList(key));
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	// ============================String=============================

	/**
	 * 普通缓存获取
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:37:31
	 * @param key
	 *            键
	 * @return 值
	 * @description
	 */
	public static Object get(String key) {
		try {
			return key == null ? null : redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	/**
	 * 普通缓存放入
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:42:44
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true:成功,false:失败
	 * @description
	 */
	public static boolean set(String key, Object value) {

		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:43:41
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true:成功,false:失败
	 * @description
	 */
	public static boolean set(String key, Object value, long time) {

		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;

		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 递增
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:46:51
	 * @param key
	 *            键
	 * @param delta
	 *            要增加几(大于0)
	 * @return
	 * @description
	 */
	public static long incr(String key, long delta) {

		try {
			if (delta < 0) {
				throw new RuntimeException("递增因子必须大于0");
			}
			return redisTemplate.opsForValue().increment(key, delta);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 递减
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:48:38
	 * @param key
	 *            键
	 * @param delta
	 *            要减少几(小于0)
	 * @return
	 * @description
	 */
	public static long decr(String key, long delta) {

		try {
			if (delta < 0) {
				throw new RuntimeException("递减因子必须大于0");
			}
			return redisTemplate.opsForValue().increment(key, -delta);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	// ================================Map=================================

	/**
	 * HashGet
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:50:12
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 不能为null
	 * @return 值
	 * @description
	 */
	public static Object hget(String key, String item) {

		try {
			return redisTemplate.opsForHash().get(key, item);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	/**
	 * 获取hashKey对应的所有键值
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:53:47
	 * @param key
	 *            键
	 * @return 对应的多个键值
	 * @description
	 */
	public static Map<Object, Object> hmget(String key) {

		try {
			return redisTemplate.opsForHash().entries(key);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * HashSet
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:55:03
	 * @param key
	 *            键
	 * @param map
	 *            对应多个键值
	 * @return true 成功 false 失败
	 * @description
	 */
	public static boolean hmset(String key, Map<String, Object> map) {

		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * HashSet 并设置时间
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:56:04
	 * @param key
	 *            键
	 * @param map
	 *            对应多个键值
	 * @param time
	 *            时间(秒)
	 * @return true成功 false失败
	 * @description
	 */
	public static boolean hmset(String key, Map<String, Object> map, long time) {

		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:57:26
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param value
	 *            值
	 * @return true 成功 false失败
	 * @description
	 */
	public static boolean hset(String key, String item, Object value) {

		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建,并设置时间
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:59:29
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 * @description
	 */
	public static boolean hset(String key, String item, Object value, long time) {

		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 删除hash表中的值
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午12:00:39
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 可以是多个 不能为null
	 * @description
	 */
	public static boolean hdel(String key, Object... item) {

		try {
			redisTemplate.opsForHash().delete(key, item);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 判断hash表中是否有该项的值
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午12:02:52
	 * @param key
	 *            键 不能为null
	 * @param item
	 *            项 不能为null
	 * @return true 存在 false不存在
	 * @description
	 */
	public static boolean hHasKey(String key, String item) {

		try {
			return redisTemplate.opsForHash().hasKey(key, item);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午12:04:27
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param by
	 *            要增加几(大于0)
	 * @return
	 * @description
	 */
	public static double hincr(String key, String item, double by) {
		try {
			return redisTemplate.opsForHash().increment(key, item, by);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * hash递减
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:34:21
	 * @param key
	 *            键
	 * @param item
	 *            项
	 * @param by
	 *            要减少记(小于0)
	 * @return
	 * @description
	 */
	public static double hdecr(String key, String item, double by) {

		try {
			return redisTemplate.opsForHash().increment(key, item, -by);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	// ============================set=============================

	/**
	 * 根据key获取Set中的所有值
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:41:12
	 * @param key
	 *            键
	 * @return
	 * @description
	 */
	public static Set<Object> sGet(String key) {

		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 根据value从一个set中查询,是否存在
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:49:43
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 * @description
	 */
	public static boolean sHasKey(String key, Object value) {

		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将数据放入set缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:50:50
	 * @param key
	 *            键
	 * @param values
	 *            值 可以是多个
	 * @return 成功个数
	 * @description
	 */
	public static long sSet(String key, Object... values) {

		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将set数据放入缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:51:35
	 * @param key
	 *            键
	 * @param time
	 *            时间(秒)
	 * @param values
	 *            值 可以是多个
	 * @return 成功个数
	 * @description
	 */
	public static long sSetAndTime(String key, long time, Object... values) {

		try {
			Long count = redisTemplate.opsForSet().add(key, values);
			if (time > 0)
				expire(key, time);
			return count;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 获取set缓存的长度
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:53:14
	 * @param key
	 *            键
	 * @return
	 * @description
	 */
	public static long sGetSetSize(String key) {

		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 移除值为value的
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:54:06
	 * @param key
	 *            键
	 * @param values
	 *            值 可以是多个
	 * @return 移除的个数
	 * @description
	 */
	public static long setRemove(String key, Object... values) {

		try {
			Long count = redisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:55:27
	 * @param key
	 *            键
	 * @param start
	 *            开始
	 * @param end
	 *            结束 0 到 -1代表所有值
	 * @return
	 * @description
	 */
	public static List<Object> lGet(String key, long start, long end) {

		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 获取list缓存的长度
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:57:43
	 * @param key
	 *            键
	 * @return
	 * @description
	 */
	public static long lGetListSize(String key) {

		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 通过索引 获取list中的值
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:58:44
	 * @param key
	 *            键
	 * @param index
	 *            索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 * @description
	 */
	public static Object lGetIndex(String key, long index) {

		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将list放入缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午1:59:25
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 * @description
	 */
	public static boolean lSet(String key, Object value) {

		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将list放入缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午2:00:22
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 * @description
	 */
	public static boolean lSet(String key, Object value, long time) {

		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0)
				expire(key, time);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将list放入缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午2:01:59
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 * @description
	 */
	public static boolean lSet(String key, List<Object> value) {

		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 将list放入缓存
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午2:03:51
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param time
	 *            时间(秒)
	 * @return
	 * @description
	 */
	public static boolean lSet(String key, List<Object> value, long time) {

		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0)
				expire(key, time);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 根据索引修改list中的某条数据
	 * 
	 * @author lzd
	 * @date 2019年7月16日:下午2:05:02
	 * @param key
	 *            键
	 * @param index
	 *            索引
	 * @param value
	 *            值
	 * @return
	 * @description
	 */
	public static boolean lUpdateIndex(String key, long index, Object value) {

		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}

	/**
	 * 移除N个值为value
	 * 
	 * @author lzd
	 * @date 2019年7月16日:上午11:39:33
	 * @param key
	 *            键
	 * @param count
	 *            移除多少个
	 * @param value
	 *            值
	 * @return 移除的个数
	 * @description
	 */
	public static long lRemove(String key, long count, Object value) {

		try {
			Long remove = redisTemplate.opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return 0;
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}

	}
	
	/**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
	 * @throws Exception 
     */
	public static boolean tryGetDistibutedLock(String lockKey, String requestId, long expireTime){
		try {
			Jedis jedis=(Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
			String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
			if (LOCK_SUCCESS.equals(result)) {
		        return true;
		    }
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
		return false;
	}
	
	/**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @param retryCount 重试次数
     * @return 是否获取成功
     */
	public static boolean tryGetDistibutedLock(String lockKey, String requestId, long expireTime,int retryCount){
		try {
			if(retryCount<=0){
				//无限循环，一直尝试加锁
				while(true){
					boolean tryGetDistibutedLock = tryGetDistibutedLock(lockKey, requestId, expireTime);
					if(tryGetDistibutedLock){
						return tryGetDistibutedLock;
					}
					Thread.sleep(2000);
				}
			}else{
				for (int i = 0; i < retryCount; i++) {
					boolean tryGetDistibutedLock = tryGetDistibutedLock(lockKey, requestId, expireTime);
					if(tryGetDistibutedLock){
						return tryGetDistibutedLock;
					}
					Thread.sleep(2000);
				}
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
		return false;
	}
	
	/**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(String lockKey, String requestId) {
    	try {
    		Jedis jedis=(Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        	//Lua脚本代码：eval()方法是将Lua代码交给Redis服务端执行。
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		} finally {
			if (redisTemplate!=null) {
				// 返回连接数
				RedisConnectionUtils.unbindConnection(redisTemplate.getConnectionFactory());
			}
		}
        return false;
    }

	
	
}
