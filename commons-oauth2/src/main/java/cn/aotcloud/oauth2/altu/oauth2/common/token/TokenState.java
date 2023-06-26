package cn.aotcloud.oauth2.altu.oauth2.common.token;

/**
 * @author xkxu
 */
public interface TokenState {

	long getCreationTime();
	
	boolean isExpired();
	
	long getLoginTime();
}
