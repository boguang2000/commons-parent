package cn.aotcloud.oauth2.altu.oauth2.common.token;

import java.util.Collection;
import java.util.List;

/**
 * @author xkxu
 */
public interface TokenStore {

	public void addToken(OAuthToken token);
	
	public void deleteToken(String username, String clientId);
	
	public void deleteToken(String accessToken);
	
	public void updateToken(OAuthToken token);
	
	public Collection<String> getAccessTokens(String username);
	
	public Collection<String> getAccessTokens(String username, String sessionName);
	
	public List<OAuthToken> getTokens(String username, String clientId);
	
	public OAuthToken getToken(String accessToken);
	
	public OAuthToken getTokenByRefreshToken(String refreshToken);
	
	public Collection<OAuthToken> getOAuthTokens();
	
	public OAuthRefreshToken getOAuthRefreshToken(String refreshToken);
}
