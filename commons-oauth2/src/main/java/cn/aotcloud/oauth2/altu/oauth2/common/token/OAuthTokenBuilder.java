package cn.aotcloud.oauth2.altu.oauth2.common.token;

/**
 * @author xkxu
 */
public class OAuthTokenBuilder {

	private BasicOAuthToken token = new BasicOAuthToken();
	
	public static OAuthTokenBuilder getBuilder() {
		return new OAuthTokenBuilder();
	}
	
	public OAuthToken getOAuthToken() {
		long time = System.currentTimeMillis();
		token.setCreationTime(time);
		token.setLoginTime(time);
		return token;
	}
	
	public OAuthTokenBuilder setAccessToken(String accessToken) {
		token.setAccessToken(accessToken);
		return this;
	}
	
	public OAuthTokenBuilder setExpiresIn(Long expiresIn) {
		token.setExpiresIn(expiresIn);
		return this;
	}

	public OAuthTokenBuilder setTokenType(String tokenType) {
		token.setTokenType(tokenType);
		return this;
	}
	
	public OAuthTokenBuilder setRefreshToken(String refreshToken, Long expiresIn) {
		BasicOAuthRefreshToken authRefreshToken = 
				new BasicOAuthRefreshToken(expiresIn, refreshToken, token);
		token.setAuthRefreshToken(authRefreshToken);
		return this;
	}

	public OAuthTokenBuilder setScope(String scope) {
		token.setScope(scope);
		return this;
	}

	public OAuthTokenBuilder setClientId(String clientId) {
		token.setClientId(clientId);
		return this;
	}
	
	public OAuthTokenBuilder setSessionName(String sessionName) {
		token.setSessionName(sessionName);
		return this;
	}

	public OAuthTokenBuilder setUsername(String username) {
		token.setUsername(username);
		return this;
	}
	
	public OAuthTokenBuilder setUid(String uid) {
		token.setUid(uid);
		return this;
	}
	
	
	public OAuthTokenBuilder setTicketId(String ticketId) {
		token.setTicketId(ticketId);
		return this;
	}
}
