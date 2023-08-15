package cn.aotcloud.mybatis.plus;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import cn.aotcloud.mybatis.annotation.EncryptedColumn;
import cn.aotcloud.mybatis.annotation.EncryptedTable;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 加密拦截器
 */
@SuppressWarnings({"rawtypes"})
public abstract class ColumnEncryptInterceptor extends JsqlParserSupport implements InnerInterceptor {
	
    /**
     * 变量占位符正则
     */
    protected Pattern PARAM_PAIRS_RE = Pattern.compile("#\\{ew\\.paramNameValuePairs\\.(" + Constants.WRAPPER_PARAM + "\\d+)\\}");

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        InnerInterceptor.super.beforePrepare(sh, connection, transactionTimeout);
    }

    @Override
    public void beforeGetBoundSql(StatementHandler sh) {
        // 只有 BatchExecutor（批处理） 和 ReuseExecutor（复用sqlSession） 才会调用到这个方法
        InnerInterceptor.super.beforeGetBoundSql(sh);
    }

    /**
     * 如果查询条件是加密数据列，那么要将查询条件进行数据加密。
     * 例如，手机号加密存储后，按手机号查询时，先把要查询的手机号进行加密，再和数据库存储的加密数据进行匹配
     */
    @SuppressWarnings("unchecked")
	@Override
    public void beforeQuery(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (Objects.isNull(parameterObject)) {
            return;
        }
        if (!(parameterObject instanceof Map)) {
            return;
        }
        Map paramMap = (Map) parameterObject;
        // 参数去重，否则多次加密会导致查询失败
        Set set = (Set) paramMap.values().stream().collect(Collectors.toSet());
        for (Object param : set) {
            /**
             *  仅支持类型是自定义Entity的参数，不支持mapper的参数是QueryWrapper、String等，例如：
             *
             *  支持：findList(@Param(value = "query") UserEntity query);
             *  支持：findPage(@Param(value = "query") UserEntity query, Page<UserEntity> page);
             *
             *  不支持：findList(@Param(value = "mobile") String mobile);
             *  不支持：findList(QueryWrapper wrapper);
             */
            if (param instanceof AbstractWrapper || param instanceof String) {
                // Wrapper、String类型查询参数，无法获取参数变量上的注解，无法确认是否需要加密，因此不做判断
                continue;
            }
            if (needToDecrypt(param.getClass())) {
                encryptEntity(param);
            }
        }
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement mappedStatement, Object parameterObject) throws SQLException {
        if (Objects.isNull(parameterObject)) {
            return;
        }
        // 通过MybatisPlus自带API（save、insert等）新增数据库时
        if (!(parameterObject instanceof Map)) {
            if (needToDecrypt(parameterObject.getClass())) {
                encryptEntity(parameterObject);
            }
            return;
        }
        Map paramMap = (Map) parameterObject;
        Object param;
        // 通过MybatisPlus自带API（update、updateById等）修改数据库时
        if (paramMap.containsKey(Constants.ENTITY) && null != (param = paramMap.get(Constants.ENTITY))) {
            if (needToDecrypt(param.getClass())) {
                encryptEntity(param);
            }
            return;
        }
        // 通过在mapper.xml中自定义API修改数据库时
        if (paramMap.containsKey("entity") && null != (param = paramMap.get("entity"))) {
            if (needToDecrypt(param.getClass())) {
                encryptEntity(param);
            }
            return;
        }
        // 通过UpdateWrapper、LambdaUpdateWrapper修改数据库时
        if (paramMap.containsKey(Constants.WRAPPER) && null != (param = paramMap.get(Constants.WRAPPER))) {
            if (param instanceof Update && param instanceof AbstractWrapper) {
                Class<?> entityClass = mappedStatement.getParameterMap().getType();
                if (needToDecrypt(entityClass)) {
                    encryptWrapper(entityClass, param);
                }
            }
            return;
        }
    }

    /**
     * 校验该实例的类是否被@EncryptedTable所注解
     */
    private boolean needToDecrypt(Class<?> objectClass) {
        EncryptedTable sensitiveData = AnnotationUtils.findAnnotation(objectClass, EncryptedTable.class);
        return Objects.nonNull(sensitiveData);
    }

    /**
     * 通过API（save、updateById等）修改数据库时
     *
     * @param parameter
     */
    private void encryptEntity(Object parameter) {
        //取出parameterType的类
        Class<?> resultClass = parameter.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (!Objects.isNull(sensitiveField)) {
                field.setAccessible(true);
                Object object = null;
                try {
                    object = field.get(parameter);
                } catch (IllegalAccessException e) {
                    continue;
                }
                //只支持String的解密
                if (object instanceof String) {
                    String value = (String) object;
                    //对注解的字段进行逐一加密
                    try {
                        field.set(parameter, this.encryptData(value));
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * 通过UpdateWrapper、LambdaUpdateWrapper修改数据库时
     *
     * @param entityClass
     * @param ewParam
     */
    @SuppressWarnings("unchecked")
	private void encryptWrapper(Class<?> entityClass, Object ewParam) {
        AbstractWrapper updateWrapper = (AbstractWrapper) ewParam;
        String sqlSet = updateWrapper.getSqlSet();
        if (StringUtils.isBlank(sqlSet)) {
            return;
        }
        String[] elArr = sqlSet.split(",");
        Map<String, String> propMap = new HashMap<>(elArr.length);
        Arrays.stream(elArr).forEach(el -> {
            String[] elPart = el.split("=");
            propMap.put(elPart[0], elPart[1]);
        });

        //取出parameterType的类
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (Objects.isNull(sensitiveField)) {
                continue;
            }
            String fieldName = field.getName();
            String fieldValue = propMap.get(fieldName);
            if(StringUtils.isNotBlank(fieldValue)) {
	            Matcher matcher = PARAM_PAIRS_RE.matcher(fieldValue);
	            if (matcher.matches()) {
	                String valueKey = matcher.group(1);
	                Object value = updateWrapper.getParamNameValuePairs().get(valueKey);
	                updateWrapper.getParamNameValuePairs().put(valueKey, this.encryptData(value.toString()));
	            }
            }
        }

        Method[] declaredMethods = entityClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = method.getAnnotation(EncryptedColumn.class);
            if (Objects.isNull(sensitiveField)) {
                continue;
            }
            String el = propMap.get(method.getName());
            Matcher matcher = PARAM_PAIRS_RE.matcher(el);
            if (matcher.matches()) {
                String valueKey = matcher.group(1);
                Object value = updateWrapper.getParamNameValuePairs().get(valueKey);
                updateWrapper.getParamNameValuePairs().put(valueKey, this.encryptData(value.toString()));
            }
        }
    }
    
    public abstract String encryptData(String data);
}