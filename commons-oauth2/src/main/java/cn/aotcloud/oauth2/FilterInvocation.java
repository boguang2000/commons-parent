package cn.aotcloud.oauth2;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xkxu
 */
public class FilterInvocation {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain filterChain;
	private String requestUri;

	public FilterInvocation(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
			String requestUri) {
		this.request = request;
		this.response = response;
		this.filterChain = filterChain;
		this.requestUri = requestUri;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public FilterChain getFilterChain() {
		return filterChain;
	}

	public String getRequestUri() {
		return requestUri;
	}
}
