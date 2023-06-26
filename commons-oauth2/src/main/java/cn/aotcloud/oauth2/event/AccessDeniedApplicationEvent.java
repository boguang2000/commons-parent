package cn.aotcloud.oauth2.event;

import org.springframework.context.ApplicationEvent;

import cn.aotcloud.oauth2.altu.oauth2.rsfilter.OAuthDecision;

/**
 * 用户越权访问事件
 * 
 * @author xkxu
 */
public class AccessDeniedApplicationEvent extends ApplicationEvent {

	private static final long serialVersionUID = 8791172596554075851L;
	
	private String requestUri;

	public AccessDeniedApplicationEvent(Object source, String requestUri) {
		super(source);
		this.requestUri = requestUri;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public OAuthDecision getOAuthDecision() {
		return (OAuthDecision) getSource();
	}
}
