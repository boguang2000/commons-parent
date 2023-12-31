package cn.aotcloud.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import cn.aotcloud.filter.BodyHttpRequestWrapper;

public class ServletUtils {

	public static HttpServletRequest convertRequest(ServletRequest req) {
		return (HttpServletRequest) req;
	}

	public static BodyHttpRequestWrapper getBodyRequest(ServletRequest req) throws IOException {
		if (req instanceof BodyHttpRequestWrapper) {
			return (BodyHttpRequestWrapper) req;
		} else {
			return new BodyHttpRequestWrapper((HttpServletRequest) req);
		}
	}

	public static HttpServletResponse convertResponse(ServletResponse res) {
		return (HttpServletResponse) res;
	}
	
	public static boolean isXwwwFormUrlencoded(HttpServletRequest request) {
		String contentType = HttpServletUtil.getHeader(request, "Content-Type");
		return StringUtils.indexOf(StringUtils.lowerCase(contentType), "application/x-www-form-urlencoded") != -1;
	}

	public static boolean isApplicationJson(HttpServletRequest request) {
		String contentType = HttpServletUtil.getHeader(request, "Content-Type");
		return StringUtils.indexOf(StringUtils.lowerCase(contentType), "application/json") != -1;
	}

	public static boolean isTextPlain(HttpServletRequest request) {
		String contentType = HttpServletUtil.getHeader(request, "Content-Type");
		return StringUtils.indexOf(StringUtils.lowerCase(contentType), "text/plain") != -1;
	}

	public static boolean isMultipartFormData(HttpServletRequest request) {
		String contentType = HttpServletUtil.getHeader(request, "Content-Type");
		return StringUtils.indexOf(StringUtils.lowerCase(contentType), "multipart/form-data") != -1;
	}

	public static Charset readCharacterEncoding(HttpServletRequest request) {
		Charset charset = null;
		String characterEncoding = HttpServletUtil.getCharacterEncoding(request);
		if (StringUtils.isNotBlank(characterEncoding) && !StringUtils.equalsIgnoreCase(characterEncoding, "null")) {
			charset = Charset.forName(characterEncoding);
		} else {
			charset = Charset.defaultCharset();
		}
		return charset;
	}
}
