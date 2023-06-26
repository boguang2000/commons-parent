package cn.aotcloud.oauth2;

import com.alibaba.fastjson.JSONObject;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.utils.HttpServletUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关截获的URL辅助类
 * 
 * @author xkxu
 */
public class ZuulUrlHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(ZuulUrlHelper.class);
	
	/**
	 * @return 获得请求的URL地址，如果是通过zuul访问网关进入的，则需要特殊处理。
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		String proto = HttpServletUtil.getHeader(request, "X-Acloud-Protocol");
		if (proto == null) {
			proto = HttpServletUtil.getHeader(request, "x-forwarded-proto");
		}
		if (proto == null) {
			proto = request.getScheme();
		}

		String urlPrefix = HttpServletUtil.getHeader(request, "X-Acloud-Request-Uri-Prefix");
		if (urlPrefix == null) {
			urlPrefix = "";
		}

		String host = HttpServletUtil.getHeader(request, "x-forwarded-host");
		String serviceId = getServiceId(request);
		String url = null;
		if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(serviceId)) {
			url = proto + "://" + host + urlPrefix + serviceId + request.getRequestURI();
		} else {
			url = proto + "://" + request.getServerName() + ":" + request.getServerPort() + urlPrefix + request.getContextPath()
					+ request.getRequestURI();
		}
		return url;
	}

	public static String getDefaultLogoutSuccessUrl(HttpServletRequest request) {
		String serviceId = getServiceId(request);
		if (!StringUtils.isEmpty(serviceId)) {
			return serviceId + "/";
		}
		return "/";
	}

	public static String getLogoutUrl(HttpServletRequest request, String logoutPath) {

		String proto = HttpServletUtil.getHeader(request, "X-Acloud-Protocol");
		if (proto == null) {
			proto = HttpServletUtil.getHeader(request, "x-forwarded-proto");
		}
		if (proto == null) {
			proto = request.getScheme();
		}
		String host = HttpServletUtil.getHeader(request, "x-forwarded-host");
		String serviceId = getServiceId(request);

		String redirectURI = null;

		if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(serviceId)) {
			redirectURI = proto + "://" + host + serviceId + logoutPath;
		} else {
			redirectURI = proto + "://" + request.getServerName() + ":" + request.getServerPort() + logoutPath;
		}
		return redirectURI;
	}

	public static String getRedirectUri(HttpServletRequest request, String redirectUri) {
		// String redirectUri = properties.getClient().getRedirectUrl();

		//if (redirectUri == null) {
		//	HttpSession session = request.getSession(false);
		//	if (session != null) {
		//		redirectUri = (String) session.getAttribute(AuthenticationFilter.REQUEST_URL_ATTR_NAME);
		//	}
		//}
		if (StringUtils.isEmpty(redirectUri)) {
			IllegalStateException e = new IllegalStateException("无法获得重定向URL地址。"); // TODO 记录日志，后期可能会直接抛出异常
			logger.error(e.getMessage());
		}
		return redirectUri;
	}

	//public static String getRedirectUri(HttpSession session, SecurityOAuth2Properties properties) {
	//	String redirectUri = properties.getClient().getRedirectUrl();
	//
	//	if (session != null) {
	//		redirectUri = (String) session.getAttribute(AuthenticationFilter.REQUEST_URL_ATTR_NAME);
	//	}
	//	if (StringUtils.isEmpty(redirectUri)) {
	//		IllegalStateException e = new IllegalStateException("无法获得重定向URL地址。"); // TODO 记录日志，后期可能会直接抛出异常
	//		logger.error(e);
	//	}
	//	return redirectUri;
	//}
	
	public static String getServiceId(HttpServletRequest request) {
		String serviceId = HttpServletUtil.getHeader(request, "x-forwarded-prefix");
		return serviceId;
	}

	public static void loginRedirect(HttpServletResponse servletResponse, String url) throws IOException, ServletException {
		// 由于isc认证服务采用的是将一个ajax请求地址并将请求进行二次封装形成新的地址后再进行重定向所以前端
		// 存在跨域问题，所以目前采用的方式是后台响应401并将重定向地址写到响应body中，让前端进行重定向。
		try {
			HttpServletUtil.setStatus(servletResponse, HttpStatus.UNAUTHORIZED.value());
			HttpServletUtil.setHeader(servletResponse, "Strict-Transport-Security", "max-age=31536000; includeSubDomains;preload");
			FileCopyUtils.copy(url, HttpServletUtil.getPrintWriter(servletResponse));
		} catch (OAuthSystemException e) {
			throw new ServletException(e);
		}
	}

	public static void responseEmpty(HttpServletResponse servletResponse) throws IOException {
		HttpServletUtil.setStatus(servletResponse, HttpStatus.FORBIDDEN.value());
		HttpServletUtil.setCharacterEncoding(servletResponse, "UTF-8");
		HttpServletUtil.setHeader(servletResponse, "Strict-Transport-Security", "max-age=31536000; includeSubDomains;preload");
		FileCopyUtils.copy("", HttpServletUtil.getPrintWriter(servletResponse));
	}
	
	public static void responseUser(HttpServletResponse servletResponse, Map<String, Object> user, Object accessToken) throws IOException {
		// 在我们约定的登录成功url中，如果正常则直接返回用户信息
		servletResponse.setStatus(HttpStatus.OK.value());
		Map<String, Object> resultToUse = new HashMap<>();
		resultToUse.put("userInfo", user);
		resultToUse.put("accessToken", accessToken);
		HttpServletUtil.setCharacterEncoding(servletResponse, "UTF-8");
		HttpServletUtil.setContentType(servletResponse, "application/json; charset=UTF-8");
		HttpServletUtil.setHeader(servletResponse, "Strict-Transport-Security", "max-age=31536000; includeSubDomains;preload");
		FileCopyUtils.copy(JSONObject.toJSONString(resultToUse), HttpServletUtil.getPrintWriter(servletResponse));
//		FileCopyUtils.copy(new String(JSONObject.toJSONString(resultToUse).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), HttpServletUtil.getPrintWriter(servletResponse));
	}
}
