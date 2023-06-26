package cn.aotcloud.oauth2;

import org.apache.commons.lang3.StringUtils;
//import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;

import cn.aotcloud.oauth2.filter.BaseAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * 注销操作URL构建器
 * 
 * @author xkxu
 */
public class LogoutUrlBuilder {
	
	private String authorizationUrl;
	private String sloUrl;
	private String clientId;
	private String accessToken;
	private String logoutUri;
	private String logoutType;

	@Deprecated
	public LogoutUrlBuilder(Environment environment) {
		//RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "security.oauth2.client.");
		Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
		Binder binder = new Binder(sources);
		BindResult<Properties> bindResult = binder.bind("spring.datasource", Properties.class);
		Properties propertyResolver= bindResult.get();	
		
		this.authorizationUrl = propertyResolver.getProperty("user-authorization-uri");
		this.sloUrl = propertyResolver.getProperty("user-slo-uri");
		this.clientId = propertyResolver.getProperty("client-id");
		this.logoutUri = propertyResolver.getProperty("logout-uri");
	}

	@Deprecated
	public LogoutUrlBuilder(Environment environment, HttpServletRequest request, String accessTokenAttrName) {
		this(environment);
		this.accessToken = BaseAuthenticationFilter.getAccessToken(request, accessTokenAttrName);
	}

	public LogoutUrlBuilder(String authorizationUrl, String clientId, String logoutUri) {
		this.authorizationUrl = authorizationUrl;
		this.clientId = clientId;
		this.logoutUri = logoutUri;
	}

	public String build(HttpServletRequest request) {

		StringBuilder url = new StringBuilder();

		try {
			if (StringUtils.isNotBlank(sloUrl)) {
				url.append(sloUrl);
			} else {
				URL url2 = new URL(authorizationUrl);

				url.append(url2.getProtocol()).append("://").append(url2.getHost()).append(":").append(url2.getPort())
						.append("/logout");
			}
		} catch (MalformedURLException e) {
		}

		url.append("?clientId=").append(clientId).append("&access_token=").append(getAccessToken(request));

		if (StringUtils.isBlank(logoutUri)) {
			logoutUri = ZuulUrlHelper.getLogoutUrl(request, "/logout");
		}
		url.append("&redirect_uri=").append(logoutUri);
		
		if (StringUtils.isNotBlank(logoutType)) {
			url.append("&logout_type=").append(logoutType);
		}

		return url.toString();
	}

	public String getAuthorizationUrl() {
		return authorizationUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getAccessToken(HttpServletRequest request) {
		if (accessToken == null) {
			accessToken = cn.aotcloud.oauth2.OAuthDecisionHolder.getAccessToken();
		}
		return accessToken;
	}
	
	/**
	 * 单点注销
	 * 
	 * @return
	 */
	public LogoutUrlBuilder setSlo() {
		this.logoutType = "slo";
		return this;
	}
}
