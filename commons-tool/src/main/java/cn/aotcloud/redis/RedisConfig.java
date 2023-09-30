package cn.aotcloud.redis;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * bgu
 */
public class RedisConfig {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final RedisProperties redisProperties;
	
	public RedisConfig(RedisProperties redisProperties) {
		this.redisProperties = redisProperties;
	}
	
	public LettuceConnectionFactory lettuceConnectionFactory(RedisProperties redisProperties) {
		GenericObjectPoolConfig<Object> genericObjectPoolConfig = null;
		LettucePoolingClientConfiguration clientConfig = null;
		Lettuce lettuce = redisProperties.getLettuce();
		if(lettuce != null) {
			Pool lettucePool = redisProperties.getLettuce().getPool();
			if(lettucePool != null) {
				genericObjectPoolConfig = new GenericObjectPoolConfig<Object>();
		        genericObjectPoolConfig.setMaxIdle(lettucePool.getMaxIdle());
		        genericObjectPoolConfig.setMinIdle(lettucePool.getMinIdle());
		        genericObjectPoolConfig.setMaxTotal(lettucePool.getMaxActive());
		        if(lettucePool.getMaxWait() != null) {
		        	genericObjectPoolConfig.setMaxWait(Duration.ofMillis(lettucePool.getMaxWait().getSeconds() * 1000));
		        }
		        if(lettucePool.getTimeBetweenEvictionRuns() != null) {
		        	genericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(lettucePool.getTimeBetweenEvictionRuns().getSeconds() * 1000));
		        }
			}
			
			LettucePoolingClientConfigurationBuilder lettucePoolingClientConfigurationBuilder = LettucePoolingClientConfiguration.builder();
			if(redisProperties.getTimeout() != null) {
				lettucePoolingClientConfigurationBuilder.commandTimeout(redisProperties.getTimeout());
			}
			if(lettuce.getShutdownTimeout() != null) {
				lettucePoolingClientConfigurationBuilder.shutdownTimeout(lettuce.getShutdownTimeout());
			}
			if(genericObjectPoolConfig != null) {
				lettucePoolingClientConfigurationBuilder.poolConfig(genericObjectPoolConfig);
			}
			clientConfig = lettucePoolingClientConfigurationBuilder.build();
		}
			
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        if(StringUtils.isNotBlank(redisProperties.getUsername())) {
        	redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
		}
        if(StringUtils.isNotBlank(redisProperties.getPassword())) {
        	redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
		}
        
        if(clientConfig != null) {
        	//factory.setShareNativeConnection(true);
            //factory.setValidateConnection(false);
        	return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        } else {
        	return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }
	
}