package cn.aotcloud.redis;

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

import cn.aotcloud.exception.BaseExceptionEmpty;
import cn.aotcloud.exception.ExceptionUtil;
import cn.aotcloud.prop.RedisSafeProperties;
import cn.aotcloud.smcrypto.Sm3Utils;
import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;
import cn.aotcloud.smcrypto.exception.InvalidSourceDataException;

/**
 * bgu
 */
public class RedisConfig {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final RedisSafeProperties redisSafeProperties;
	
	protected final RedisProperties redisProperties;
	
	public RedisConfig(RedisSafeProperties redisSafeProperties, RedisProperties redisProperties) {
		this.redisSafeProperties = redisSafeProperties;
		this.redisProperties = redisProperties;
	}
	
    @SuppressWarnings("deprecation")
	public LettuceConnectionFactory lettuceConnectionFactory(String sm4K, String sm4v, String salt) {
    	String host = this.getHost(sm4K, sm4v, salt);
    	String username = this.getUsername(sm4K, sm4v, salt);
    	String password = this.getPassword(sm4K, sm4v, salt);
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
		        	genericObjectPoolConfig.setMaxWaitMillis(lettucePool.getMaxWait().getSeconds() * 1000);
		        }
		        if(lettucePool.getTimeBetweenEvictionRuns() != null) {
		        	genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(lettucePool.getTimeBetweenEvictionRuns().getSeconds() * 1000);
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
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        if(StringUtils.isNotBlank(username)) {
        	redisStandaloneConfiguration.setUsername(username);
		}
        if(StringUtils.isNotBlank(password)) {
        	redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		}
        
        if(clientConfig != null) {
        	//factory.setShareNativeConnection(true);
            //factory.setValidateConnection(false);
        	return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        } else {
        	return new LettuceConnectionFactory(redisStandaloneConfiguration);
        }
    }
	
	public String getHost(String sm4K, String sm4v, String salt) {
		String dz = redisSafeProperties.getDz();
		if(StringUtils.isNotBlank(dz)) {
			if(StringUtils.startsWith(dz, "enc(")) {
				try {
					String dze = StringUtils.substringBetween(dz, "enc(", "|");
					String sm3 = StringUtils.substringBetween(dz, "|", ")");
					dz = Sm4Utils.CBC.decryptToText(dze, sm4K, sm4v);
					if(!verifySm3(dz, salt, sm3)) {
						throw new BaseExceptionEmpty("Redis地址被篡改");
					}
					logger.info("Redis地址解密后装载成功");
				} catch (InvalidCryptoDataException e) {
					logger.error("Redis地址解密失败：{}", e.getMessage());
				} catch (InvalidKeyException e) {
					logger.error("Redis地址解密失败：{}", e.getMessage());
				}
			} else {
				logger.info("Redis地址明文装载成功");
			}
			return dz;
		} else {
			logger.info("Redis地址默认装载成功");
			return redisProperties.getHost();
		}
	}
	
	public String getUsername(String sm4K, String sm4v, String salt) {
		String un = redisSafeProperties.getUn();
		if(StringUtils.isNotBlank(un)) {
			if(StringUtils.startsWith(un, "enc(")) {
				try {
					String une = StringUtils.substringBetween(un, "enc(", "|");
					String sm3 = StringUtils.substringBetween(un, "|", ")");
					un = Sm4Utils.CBC.decryptToText(une, sm4K, sm4v);
					if(!verifySm3(un, salt, sm3)) {
						throw new BaseExceptionEmpty("Redis用户名被篡改");
					}
					logger.info("Redis用户名解密后装载成功");
				} catch (InvalidCryptoDataException e) {
					logger.error("Redis用户名解密失败：{}", e.getMessage());
				} catch (InvalidKeyException e) {
					logger.error("Redis用户名解密失败：{}", e.getMessage());
				}
			} else {
				logger.info("Redis用户名明文装载成功");
			}
			return un;
		} else {
			logger.info("Redis用户名默认装载成功");
			return redisProperties.getUsername();
		}
	}
	
	public String getPassword(String sm4K, String sm4v, String salt) {
		String pw = redisSafeProperties.getPw();
		if(StringUtils.isNotBlank(pw)) {
			if(StringUtils.startsWith(pw, "enc(")) {
				try {
					String pwe = StringUtils.substringBetween(pw, "enc(", "|");
					String sm3 = StringUtils.substringBetween(pw, "|", ")");
					pw = Sm4Utils.CBC.decryptToText(pwe, sm4K, sm4v);
					if(!verifySm3(pw, salt, sm3)) {
						throw new BaseExceptionEmpty("Redis密码被篡改");
					}
					logger.info("Redis密码解密后装载成功");
				} catch (InvalidCryptoDataException e) {
					logger.error("Redis密码解密失败：{}", e.getMessage());
				} catch (InvalidKeyException e) {
					logger.error("Redis密码解密失败：{}", e.getMessage());
				}
			} else {
				logger.info("Redis密码明文装载成功");
			}
			return pw;
		} else {
			logger.info("Redis密码默认装载成功");
			return redisProperties.getPassword();
		}
	}
	
	private boolean verifySm3(String data, String salt, String sm3) {
		try {
			String sm3_ = Sm3Utils.encryptFromText(data + salt);
			return StringUtils.equals(sm3, sm3_);
		} catch (InvalidSourceDataException e) {
			logger.error("SM3计算异常：{}", ExceptionUtil.getMessage(e));
		}
		
		return false;
	}
}