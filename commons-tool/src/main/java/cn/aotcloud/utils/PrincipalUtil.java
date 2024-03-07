package cn.aotcloud.utils;

import org.apache.commons.lang3.StringUtils;

public class PrincipalUtil {

	public static boolean comparePlantText(String text, String qrText) {
		if(StringUtils.isNotBlank(qrText) && StringUtils.isNotBlank(text)) {
			return text.matches(qrText);
		} else {
			return false;
		}
	}
	
	public static String equals(String text) {
		return text;
	}
}
