package cn.aotcloud.oauth2.altu.oauth2.rsfilter;

import java.io.Serializable;
import java.security.Principal;

/**
 * 
 * @author xkxu
 *
 */
public class SimpleOAuthDecision implements OAuthDecision, Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean authorized;
	private Principal principal;
	private OAuthClient oAuthClient;
	private String accessToken;
	
	
	public SimpleOAuthDecision() {
	}
	
	public SimpleOAuthDecision(String owner, String clientId) {
		this.authorized = true;
		this.principal = new SimplePrincipal(owner);
		this.oAuthClient = new SimpleOAuthClient(clientId);
	}

	@Override
	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	@Override
	public OAuthClient getOAuthClient() {
		return oAuthClient;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public void setOAuthClient(OAuthClient oAuthClient) {
		this.oAuthClient = oAuthClient;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
