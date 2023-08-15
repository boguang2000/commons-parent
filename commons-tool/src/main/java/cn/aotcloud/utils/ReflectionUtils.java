package cn.aotcloud.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

import org.springframework.core.annotation.AnnotationUtils;

public class ReflectionUtils {

	public static <A extends Annotation> boolean isAnnotation(Object object, Class<A> clz) {
        Class<?> objectClass = object.getClass();
        A obj = AnnotationUtils.findAnnotation(objectClass, clz);
        return Objects.nonNull(obj);
    }
	
	public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> resultClass = obj.getClass();
		Field field = resultClass.getDeclaredField(fieldName);
		field.setAccessible(true);
        Object object = field.get(obj);
        return object;
	}
	
	public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> resultClass = obj.getClass();
		Field field = resultClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(obj, fieldValue);
	}
}
