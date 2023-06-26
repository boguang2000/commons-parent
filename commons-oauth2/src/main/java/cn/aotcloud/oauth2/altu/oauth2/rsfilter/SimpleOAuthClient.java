package cn.aotcloud.oauth2.altu.oauth2.rsfilter;

import java.io.Serializable;

/**
 * 
 * @author xkxu
 *
 */
public class SimpleOAuthClient implements OAuthClient, Serializable {

	private static final long serialVersionUID = 1L;
	
	private String clientId;
	

	public SimpleOAuthClient() {
	}

	public SimpleOAuthClient(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
