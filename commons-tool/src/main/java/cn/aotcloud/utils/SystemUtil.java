package cn.aotcloud.utils;

import java.util.Properties;

public class SystemUtil {

	private static Properties props = System.getProperties();
	
	public static String getProp(String name) {
		if(props != null) {
			return props.getProperty(name);
		} else {
			return null;
		}
	}
	
	public static String getUdir() {
		return SystemUtil.getProp("user.dir");
	}
	
	public static String getJVersion() {
		return SystemUtil.getProp("java.version");
	}
	
	public static String getJHome() {
		return SystemUtil.getProp("java.home");
	}
}
