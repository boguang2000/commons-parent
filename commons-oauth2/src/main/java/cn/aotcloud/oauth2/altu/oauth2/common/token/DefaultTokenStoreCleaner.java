package cn.aotcloud.oauth2.altu.oauth2.common.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTokenStoreCleaner implements TokenStoreCleaner {
	
	private TokenStore tokenStore;
	
	public DefaultTokenStoreCleaner(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@Override
	public void clean() {
		List<OAuthToken> tokensToRemove = new ArrayList<>();
		Collection<OAuthToken> tokens = tokenStore.getOAuthTokens();
		for (OAuthToken token : tokens) {
			if (token.isExpired()) {
				tokensToRemove.add(token);
			}
		}
		
		if (!tokensToRemove.isEmpty()) {
			for (OAuthToken token : tokensToRemove) {
				tokenStore.deleteToken(token.getAccessToken());
			}
		}
	}

}
