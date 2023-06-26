package cn.aotcloud.oauth2.altu.oauth2.common.token;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author xkxu
 */
public class InMemoryTokenStore implements TokenStore {

	private Map<String, OAuthToken> cache;
	private Map<String, OAuthRefreshToken> refreshCache;
	private Map<Pair<String, String>, String> ownerToAccessTokenMap;
	private Map<String, Pair<String, String>> accessTokenToOwnerMap;
	private Map<String, String> refreshToAccessMap;

	public InMemoryTokenStore() {
		this.cache = new ConcurrentHashMap<String, OAuthToken>();
		this.refreshCache = new  ConcurrentHashMap<String, OAuthRefreshToken>();
		this.ownerToAccessTokenMap = new ConcurrentHashMap<Pair<String, String>, String>();
		this.accessTokenToOwnerMap = new ConcurrentHashMap<String, Pair<String, String>>();
		this.refreshToAccessMap = new ConcurrentHashMap<String, String>();
	}

	public InMemoryTokenStore(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
		
		this.cache = new ConcurrentHashMap<String, OAuthToken>(
				initialCapacity, loadFactor, concurrencyLevel);
		
		this.ownerToAccessTokenMap = new ConcurrentHashMap<Pair<String, String>, String>(
				initialCapacity, loadFactor, concurrencyLevel);
		
		this.accessTokenToOwnerMap = new ConcurrentHashMap<String, Pair<String, String>>(
				initialCapacity, loadFactor, concurrencyLevel);
		
		this.refreshToAccessMap = new ConcurrentHashMap<String, String>(
				initialCapacity, loadFactor, concurrencyLevel);
	}

	@Override
	public void addToken(OAuthToken token) {
		this.cache.put(token.getAccessToken(), token);
		Pair<String, String> key = new Pair<String, String>(token.getUsername(), token.getClientId());
		this.ownerToAccessTokenMap.put(key, token.getAccessToken());
		this.accessTokenToOwnerMap.put(token.getAccessToken(), key);
		OAuthRefreshToken authRefreshToken = token.getOAuthRefreshToken();
		if (authRefreshToken != null) {
			this.refreshCache.put(authRefreshToken.getRefreshToken(), authRefreshToken);
			this.refreshToAccessMap.put(authRefreshToken.getRefreshToken(), token.getAccessToken());
		}
	}

	@Override
	public void deleteToken(String username, String clientId) {
		List<OAuthToken> tokens = getTokens(username, clientId);
		if (!CollectionUtils.isEmpty(tokens)) {
			tokens.forEach(token -> {deleteToken(token.getAccessToken());});
		}
	}

	@Override
	public void deleteToken(String accessToken) {
		OAuthToken token = this.cache.remove(accessToken);
		Pair<String, String> pair = this.accessTokenToOwnerMap.get(accessToken);
		if (pair != null) {
			this.ownerToAccessTokenMap.remove(pair);
		}
		this.accessTokenToOwnerMap.remove(accessToken);
		if (token != null && token.getRefreshToken() != null) {
			this.refreshToAccessMap.remove(token.getRefreshToken());
			this.refreshCache.remove(token.getRefreshToken());
		}
	}

	@Override
	public void updateToken(OAuthToken token) {
		deleteToken(token.getAccessToken());
		addToken(token);
	}

	@Override
	public List<OAuthToken> getTokens(String username, String clientId) {
		String[] accessTokens = getTokensAsStr(username, clientId);
		List<OAuthToken> list = new ArrayList<>();
		if (accessTokens != null) {
			for (String accessToken : accessTokens) {
				list.add(getToken(accessToken));
			}
		}
		return list;
	}
	
	private String[] getTokensAsStr(String owner, String clientId) {
		String accessToken = this.ownerToAccessTokenMap.get(new Pair<String, String>(owner, clientId));
		if (!StringUtils.isEmpty(accessToken)) {
			return accessToken.split(",");
		}
		return null;
	}

	@Override
	public OAuthToken getToken(String accessToken) {
		return this.cache.get(accessToken);
	}
	
	@Override
	public OAuthToken getTokenByRefreshToken(String refreshToken) {
		String accessToken = this.refreshToAccessMap.get(refreshToken);
		if (accessToken != null) {
			return this.cache.get(accessToken);
		}
		return null;
	}
	
	@Override
	public Collection<String> getAccessTokens(String username) {
		return cache.values()
			.stream()
			.filter((token) -> {return token.getUsername().equals(username);})
			.map(OAuthToken::getAccessToken)
			.collect(Collectors.toList());
	}
	
	@Override
	public Collection<String> getAccessTokens(String username, String sessionName) {
		return cache.values()
		.stream()
		.filter((token) -> {return token.getUsername().equals(username) && StringUtils.equals(token.getSessionName(), sessionName);})
		.map(OAuthToken::getAccessToken)
		.collect(Collectors.toList());
	}
	
	@Override
	public Collection<OAuthToken> getOAuthTokens() {
		return Collections.unmodifiableCollection(cache.values());
	}
	
	@Override
	public OAuthRefreshToken getOAuthRefreshToken(String refreshToken) {
		return this.refreshCache.get(refreshToken);
	}

	public static class Pair<S, T> implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private S first;
		private T second;
		
		public Pair(S first, T second) {
			this.first = first;
			this.second = second;
		}
		
		public S getFirst() {
			return first;
		}
		
		public T getSecond() {
			return second;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result
					+ ((second == null) ? 0 : second.hashCode());
			return result;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (first == null) {
				if (other.first != null)
					return false;
			} else if (!first.equals(other.first))
				return false;
			if (second == null) {
				if (other.second != null)
					return false;
			} else if (!second.equals(other.second))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Pair [first=" + first + ", second=" + second + "]";
		}
		
	}
}
