package cn.aotcloud.oauth2.altu.oauth2.rs.extractor;


import javax.servlet.http.HttpServletRequest;

import cn.aotcloud.oauth2.altu.oauth2.common.OAuth;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;


/**
 * 
 * @author xkxu
 *
 */
public class BearerCookieTokenExtractor implements TokenExtractor {
	
	@Override
	public String getAccessToken(HttpServletRequest request) {
		return getAccessToken(request, OAuth.Cookie.COOKIE_NAME);
	}

	@Override
	public String getAccessToken(HttpServletRequest request, String tokenName) {
		return getCookieValue(request, tokenName);
	}

	protected String getCookieValue(HttpServletRequest request, String cookieName) {
		return OAuthUtils.getCookieValue(request, cookieName);
	}
}
