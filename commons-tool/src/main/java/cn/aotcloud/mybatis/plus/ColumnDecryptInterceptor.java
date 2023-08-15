package cn.aotcloud.mybatis.plus;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.springframework.core.annotation.AnnotationUtils;

import cn.aotcloud.exception.ExceptionUtil;
import cn.aotcloud.logger.LoggerHandle;
import cn.aotcloud.mybatis.annotation.EncryptedColumn;
import cn.aotcloud.mybatis.annotation.EncryptedTable;
import cn.aotcloud.smcrypto.util.StringUtils;
import cn.aotcloud.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public abstract class ColumnDecryptInterceptor implements Interceptor {
	
	protected LoggerHandle logger = new LoggerHandle(getClass());

    @SuppressWarnings("unchecked")
	@Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object resultObject = invocation.proceed();
        if (Objects.isNull(resultObject)) {
            return null;
        }
        if (resultObject instanceof ArrayList) {
            //基于selectList
            ArrayList<Object> resultList = (ArrayList<Object>) resultObject;
            if (!resultList.isEmpty() && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {
                    //逐一解密
                    decrypt(result);
                }
            }
        } else if (needToDecrypt(resultObject)) {
            //基于selectOne
            decrypt(resultObject);
        }
        return resultObject;
    }

    /**
     * 校验该实例的类是否被@EncryptedTable所注解
     */
    private boolean needToDecrypt(Object object) {
        Class<?> objectClass = object.getClass();
        EncryptedTable sensitiveData = AnnotationUtils.findAnnotation(objectClass, EncryptedTable.class);
        return Objects.nonNull(sensitiveData);
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    private <T> T decrypt(T result) throws Exception {
        //取出resultType的类
        Class<?> resultClass = result.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (!Objects.isNull(sensitiveField)) {
                field.setAccessible(true);
                Object object = field.get(result);
                //只支持String的解密
                if (object instanceof String) {
                    String value = (String) object;
                    String key = this.getKey(result);
                    String data = this.decryptData(resultClass.getSimpleName(), field.getName(), key, value);
                    field.set(result, data);
                }
            }
        }
        return result;
    }
    
    public String getKey(Object result) {
    	String val = this.getFieldStringVal(result, "agentId");
    	if(val == null) {
    		val = this.getFieldStringVal(result, "id");
    	}
    	
    	return val;
    }
    
    public String getFieldStringVal(Object result, String key) {
    	if(result != null && StringUtils.isNotBlank(key)) {
    		try {
				Object val = ReflectionUtils.getFieldValue(result, key);
				if(val != null && val instanceof String) {
					return (String)val;
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.taskInfo("[Scheduled]", "获取识别ID异常:{}", ExceptionUtil.getMessage(e));
			}
    	}
    	
    	return null;
    }
    
    public abstract String decryptData(String className, String fieldName, String key, String data);
}