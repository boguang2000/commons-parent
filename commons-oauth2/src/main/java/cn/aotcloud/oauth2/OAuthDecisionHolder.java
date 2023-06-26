package cn.aotcloud.oauth2;


import org.springframework.core.NamedThreadLocal;

import cn.aotcloud.oauth2.altu.oauth2.rsfilter.OAuthDecision;
import cn.aotcloud.oauth2.altu.oauth2.rsfilter.SimpleOAuthDecision;
import cn.aotcloud.oauth2.altu.oauth2.rsfilter.SimplePrincipal;

import java.security.Principal;
import java.util.Map;

/**
 * 授权断言持有者
 * 
 * @author xkxu
 *
 */
public class OAuthDecisionHolder{
	
	private static final NamedThreadLocal<OAuthDecision> NAMED_THREAD_LOCAL = new NamedThreadLocal<OAuthDecision>(
			"OAuth Decision Thread Local");

	public static void setOAuthDecision(OAuthDecision decision) {
		NAMED_THREAD_LOCAL.set(decision);
	}

	public static void remove() {
		NAMED_THREAD_LOCAL.remove();
	}

	public static OAuthDecision getOAuthDecision() {
		return NAMED_THREAD_LOCAL.get();
	}

	public static String getAccessToken() {
		return getOAuthDecision() != null ? ((SimpleOAuthDecision) getOAuthDecision()).getAccessToken() : null;
	}

	public static String getClientId() {
		return getOAuthDecision() != null ? getOAuthDecision().getOAuthClient().getClientId() : null;
	}

	public static Principal getPrincipal() {
		return getOAuthDecision() != null ? getOAuthDecision().getPrincipal() : null;
	}

	public static String getUid() {
		return getPrincipal() != null ? getPrincipal().getName() : null;
	}

	public static Map<String, Object> getPrincipalAttributes() {
		return getPrincipal() != null ? ((SimplePrincipal) getPrincipal()).getAttributes() : null;
	}

	public static Object getPrincipalAttribute(String name) {
		return getPrincipalAttributes() != null ? getPrincipalAttributes().get(name) : null;
	}
}
