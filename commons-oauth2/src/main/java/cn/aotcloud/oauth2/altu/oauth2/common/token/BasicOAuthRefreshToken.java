package cn.aotcloud.oauth2.altu.oauth2.common.token;

import java.io.Serializable;

/**
 * @author xkxu
 */
public class BasicOAuthRefreshToken implements OAuthRefreshToken, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected Long expiresIn;
    protected String refreshToken;
    
    protected OAuthToken authToken;
    
    public BasicOAuthRefreshToken() {
	}

	public BasicOAuthRefreshToken(Long expiresIn, String refreshToken, OAuthToken authToken) {
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.authToken = authToken;
	}

	@Override
	public String getRefreshToken() {
		return refreshToken;
	}

	@Override
	public Long getExpiresIn() {
		return expiresIn;
	}
	
	@Override
	public String getAccessToken() {
		return this.authToken.getAccessToken();
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public OAuthToken getOAuthToken() {
		return this.authToken;
	}
	
	public void setOAuthToken(OAuthToken authToken) {
		this.authToken = authToken;
	}
	
	@Override
	public boolean isExpired() {
		if (expiresIn == null) {
			return false;
		}
		return (System.currentTimeMillis() - this.getCreationTime()) > (expiresIn * 1000);
	}
	
	public long getLoginTime() {
		return this.authToken.getLoginTime();
	}
	
	public long getCreationTime() {
		return this.authToken.getCreationTime();
	}
}
