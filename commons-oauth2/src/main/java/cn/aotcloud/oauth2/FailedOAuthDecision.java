package cn.aotcloud.oauth2;


import java.security.Principal;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.rsfilter.OAuthClient;
import cn.aotcloud.oauth2.altu.oauth2.rsfilter.OAuthDecision;

/**
 * 
 * @author xkxu
 *
 */
public class FailedOAuthDecision implements OAuthDecision {

	private OAuthProblemException e;

	public FailedOAuthDecision(OAuthProblemException e) {
		this.e = e;
	}

	@Override
	public boolean isAuthorized() {
		return false;
	}

	@Override
	public Principal getPrincipal() {
		return null;
	}

	@Override
	public OAuthClient getOAuthClient() {
		return null;
	}

	public OAuthProblemException getThrowable() {
		return e;
	}
}
