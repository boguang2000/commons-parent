package cn.aotcloud.oauth2;

import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xkxu
 */
public interface RequestUriResolver {
	
	public UrlPathHelper getUrlPathHelper();

	public String getRequestURI(HttpServletRequest request);
}
