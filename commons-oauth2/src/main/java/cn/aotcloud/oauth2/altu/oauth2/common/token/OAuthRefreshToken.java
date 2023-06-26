package cn.aotcloud.oauth2.altu.oauth2.common.token;

public interface OAuthRefreshToken extends TokenState {
	
	public String getRefreshToken();

	/**
     * @return	刷新令牌的过期时间
     */
    public Long getExpiresIn();
    
    public String getAccessToken();
    
    public OAuthToken getOAuthToken();
    
    public void setOAuthToken(OAuthToken authToken);
}
