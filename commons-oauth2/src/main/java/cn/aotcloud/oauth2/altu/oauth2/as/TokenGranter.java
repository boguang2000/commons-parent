package cn.aotcloud.oauth2.altu.oauth2.as;

import cn.aotcloud.oauth2.altu.oauth2.as.request.AbstractOAuthTokenRequest;
import cn.aotcloud.oauth2.altu.oauth2.common.token.OAuthToken;

/**
 * @author xkxu
 */
public interface TokenGranter {

	OAuthToken grant(String grantType, AbstractOAuthTokenRequest tokenRequest);
}
