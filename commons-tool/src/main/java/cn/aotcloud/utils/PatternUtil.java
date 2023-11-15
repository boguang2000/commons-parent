package cn.aotcloud.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class PatternUtil {
	
	public static Pattern formatPattern(Pattern pattern) {
		pattern =  Pattern.compile(formatPattern(pattern.pattern()), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
		return pattern;
	}
	
	public static String formatPattern(String patternString) {
		char[] array = new char[3];
		int[]  intrry = new int[3];
		for(int i=0; i<intrry.length; i++) {
			switch (i) {
			case 0:
				intrry[i] = 48;
				break;
			case 1:
				intrry[i] = 45;
				break;
			case 2:
				intrry[i] = 57;
				break;
			default:
				break;
			}
		}
		for(int i=0; i<array.length; i++) {
			array[i] = (char) intrry[i];
		}
		if(StringUtils.endsWith(patternString, String.valueOf(array) + String.valueOf((char)93))) {
			patternString = StringUtils.substringBefore(patternString, String.valueOf(array));
			StringBuffer stringBuffer = new StringBuffer(patternString);
			stringBuffer.append(String.valueOf(array));
			for(int j=0; j< 128; j++) {
				switch (j) {
				case 35:
					addStringBuffer(j, stringBuffer);
					break;
				case 36:
					addStringBuffer(j, stringBuffer);
					break;
				case 38:
					addStringBuffer(j, stringBuffer);
					break;
				case 40:
					addStringBuffer(j, stringBuffer);
					break;
				case 41:
					addStringBuffer(j, stringBuffer);
					break;
				case 43:
					addStringBuffer(j, stringBuffer);
					break;
				case 45:
					addStringBuffer(j, stringBuffer);
					break;
				case 46:
					addStringBuffer(j, stringBuffer);
					break;
				case 64:
					addStringBuffer(j, stringBuffer);
					break;
				case 95:
					addStringBuffer(j, stringBuffer);
					break;
				case 123:
					addStringBuffer(j, stringBuffer);
					break;
				case 125:
					addStringBuffer(j, stringBuffer);
					break;
				case 126:
					addStringBuffer(j, stringBuffer);
					break;
				default:
					break;
				}
			}
			addStringBuffer(65288, stringBuffer);
			addStringBuffer(65289, stringBuffer);
			stringBuffer.append((char)93);
			return stringBuffer.toString();
		}
		
		return patternString;
	}
	
	public static void addStringBuffer(int c, StringBuffer stringBuffer) {
		boolean prefix = false;
		switch (c) {
		case 36:
			prefix = true;
			break;
		case 40:
			prefix = true;
			break;
		case 41:
			prefix = true;
			break;
		case 43:
			prefix = true;
			break;
		case 45:
			prefix = true;
			break;
		case 123:
			prefix = true;
			break;
		case 125:
			prefix = true;
			break;
		default:
			break;
		}
		
		if(prefix) {
			stringBuffer.append((char)92);
		}
		stringBuffer.append((char)c);
	}
	
}
