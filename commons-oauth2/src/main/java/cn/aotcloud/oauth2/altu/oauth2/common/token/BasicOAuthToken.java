/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.aotcloud.oauth2.altu.oauth2.common.token;

import java.io.Serializable;

/**
 *
 */
public class BasicOAuthToken implements OAuthToken, TokenState, Serializable {
	
	private static final long serialVersionUID = 1L;
	
    protected String accessToken;
    protected String tokenType;
    protected Long expiresIn;
    protected String refreshToken;
    protected OAuthRefreshToken authRefreshToken;
    protected String scope;
    
    private String clientId;
    private String sessionName;
	private String username;
	private String uid;
	private long creationTime;
	private long loginTime;
	private String ticketId;

    public BasicOAuthToken() {
    }

    public BasicOAuthToken(String accessToken, String tokenType, Long expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public BasicOAuthToken(String accessToken, String tokenType) {
        this(accessToken, tokenType, null, null, null);
    }

    public BasicOAuthToken(String accessToken, String tokenType, Long expiresIn) {
        this(accessToken, tokenType, expiresIn, null, null);
    }

    public BasicOAuthToken(String accessToken, String tokenType, Long expiresIn, String scope) {
        this(accessToken, tokenType, expiresIn, null, scope);
    }
    
    @Override
	public boolean isExpired() {
		if (expiresIn == null) {
			return false;
		}
		return (System.currentTimeMillis() - this.creationTime) > (expiresIn * 1000);
	}

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getRefreshToken() {
    	if (refreshToken == null) {
    		refreshToken = (this.authRefreshToken != null ? this.authRefreshToken.getRefreshToken() : null);
    	}
        return refreshToken;
    }
    
    @Override
    public OAuthRefreshToken getOAuthRefreshToken() {
    	if (authRefreshToken == null) {
    		authRefreshToken = new BasicOAuthRefreshToken(getExpiresIn(), this.refreshToken, this);
    	}
    	return authRefreshToken;
    }

    public String getScope() {
        return scope;
    }

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public OAuthRefreshToken getAuthRefreshToken() {
		return authRefreshToken;
	}

	public void setAuthRefreshToken(OAuthRefreshToken authRefreshToken) {
		this.authRefreshToken = authRefreshToken;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
}
