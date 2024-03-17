package cn.aotcloud.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import cn.aotcloud.utils.RedisUtil;

public class RedisCacheStorage<T> implements CacheStorage<T> {

	private RedisTemplate<String, T> redisSessionTemplate;

	private String prefix;
	
	private final Long expiryTime;
	
	public RedisCacheStorage(RedisConnectionFactory connectionFactory, String prefix, long expiryTime) {
		super();
		this.redisSessionTemplate = RedisUtil.redisSessionTemplate(connectionFactory);
		this.prefix = prefix + ":";
		this.expiryTime = expiryTime;
	}

	@Override
	public void addCache(String key, T value) {
		if(expiryTime < 0) {
			redisSessionTemplate.opsForValue().set(this.prefix + key, value, 24 * 365 * 10, TimeUnit.HOURS);
		} else {
			redisSessionTemplate.opsForValue().set(this.prefix + key, value, expiryTime, TimeUnit.SECONDS);
		}
	}

	@Override
	public T getCache(String key) {
		return redisSessionTemplate.opsForValue().get(this.prefix +key);
	}
	
	@Override
	public void deleteCache(String key) {
		redisSessionTemplate.delete(this.prefix + key);
	}
	
	@Override
	public Set<String> keys() {
		return redisSessionTemplate.keys(this.prefix + "*");
	}
}
