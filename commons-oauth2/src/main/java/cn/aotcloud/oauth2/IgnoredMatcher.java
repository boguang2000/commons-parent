package cn.aotcloud.oauth2;

import javax.servlet.http.HttpServletRequest;

/**
 * 白名单匹配器
 * 
 * @author xkxu
 */
public interface IgnoredMatcher {

	/**
	 * 判断请求URL是否在白名单内
	 * 
	 * @param request
	 * @return 若返回ture,则该URL在白名单内.若返回false,则该URL不在白名单内
	 */
	public boolean match(HttpServletRequest request);
}
