package cn.aotcloud.utils;

import java.lang.reflect.Field;

public class FieldUtils {

	public static void setAccessible(Field field, boolean flag) {
		field.setAccessible(flag);
	}
}
