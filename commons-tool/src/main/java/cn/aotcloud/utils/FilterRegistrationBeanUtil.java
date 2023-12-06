package cn.aotcloud.utils;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.annotation.Order;


public class FilterRegistrationBeanUtil {

	public static void setOrder(FilterRegistrationBean<?> filterRegistrationBean) {
        if(filterRegistrationBean.getFilter() != null) {
        	Object filterObject = filterRegistrationBean.getFilter();
        	Order order = filterObject.getClass().getAnnotation(Order.class);
    		if(order != null) {
    			filterRegistrationBean.setOrder(order.value());
    		}
        }
    }
}
