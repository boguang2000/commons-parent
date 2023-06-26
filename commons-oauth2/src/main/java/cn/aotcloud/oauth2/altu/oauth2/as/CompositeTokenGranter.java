package cn.aotcloud.oauth2.altu.oauth2.as;

import java.util.ArrayList;
import java.util.List;

import cn.aotcloud.oauth2.altu.oauth2.as.request.AbstractOAuthTokenRequest;
import cn.aotcloud.oauth2.altu.oauth2.common.token.OAuthToken;

/**
 * @author xkxu
 */
public class CompositeTokenGranter implements TokenGranter {

	private final List<TokenGranter> tokenGranters;

	public CompositeTokenGranter(List<TokenGranter> tokenGranters) {
		this.tokenGranters = new ArrayList<TokenGranter>(tokenGranters);
	}
	
	public OAuthToken grant(String grantType, AbstractOAuthTokenRequest tokenRequest) {
		for (TokenGranter granter : tokenGranters) {
			OAuthToken grant = granter.grant(grantType, tokenRequest);
			if (grant!=null) {
				return grant;
			}
		}
		return null;
	}
	
	public void addTokenGranter(TokenGranter tokenGranter) {
		if (tokenGranter == null) {
			throw new IllegalArgumentException("Token granter is null");
		}
		tokenGranters.add(tokenGranter);
	}
}
