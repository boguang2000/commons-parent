package cn.aotcloud.codecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {
		
	public static <T> Collection<T> removeNull(final Collection<T> source) {
		if(isEmpty(source)) {
			return source;
		} else {
		    ArrayList<T> list = new ArrayList<T>();
		    for (T value : source) {
		    	if (value != null) {
		    		list.add(value);
		    	}
		    }
		    return list;
		}
	}
	
	public static <T> List<T> distinct(final List<T> source) {
		if(source == null || isEmpty(source)) {
			return source;
		} else {
			Set<T> set = new HashSet<T>();
		    for (T value : source) {
		    	if (value != null) {
		    		if(value instanceof String) {
		    			if(StringUtils.isNotEmpty((String)value)) {
		    				set.add(value);
		    			}
		    		} else {
		    			set.add(value);
		    		}
		    	}
		    }
		    return new ArrayList<T>(set);
		}
	}
	
	public static List<String> toUpperCase(List<String> list) {
		if(list == null || list.isEmpty()) {
			return list;
		}
		List<String> newList = new ArrayList<String>();
		for(String item : list) {
			if(StringUtils.isNotEmpty(item)) {
				newList.add(item.toUpperCase());
			}
		}
		return newList;
	}
	
	public static Set<Character> arrayToUnmodifiableSet(char...array)
	{
		if(array == null) {
			return Collections.emptySet();
		}
		if(array.length == 1) {
			return Collections.singleton(array[0]);
		}
		return Collections.unmodifiableSet(arrayToSet(array));
	}
	
	public static Set<Character> strToUnmodifiableSet(String str) {
		if(str == null) {
			return Collections.emptySet();
		}
		if(str.length() == 1) {
			return Collections.singleton(str.charAt(0));
		}
		return Collections.unmodifiableSet(strToSet(str));
	}
	
	public static Set<Character> arrayToSet(char...array) {
		Set<Character> toReturn;
		if(array == null) {
			return new HashSet<Character>();
		}
		toReturn = new HashSet<Character>(array.length);
		for (char c : array) {
			toReturn.add(c);
		}
		return toReturn;
	}
	
	public static Set<Character> strToSet(String str) {
		Set<Character> set;
		if(str == null) {
			return new HashSet<Character>();
		}
		set = new HashSet<Character>(str.length());
		for(int i=0;i<str.length();i++) {
			set.add(str.charAt(i));
		}
		return set;
	}
}
