package cn.aotcloud.oauth2;


import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.rsfilter.OAuthDecision;

/**
 * @author xkxu
 */
public interface AccessDecisionManager {

	/**
	 * 验证用户是否拥有URL的访问权限。
	 *
	 * @param decision	令牌验证结果
	 * @param resource	访问的URL资源，一般是 FilterInvocation 对象
	 * @throws OAuthProblemException	无法访问抛出异常
	 *
	 * @see cn.aotcloud.oauth2.FilterInvocation
	 */
	public void decide(OAuthDecision decision, Object resource) throws OAuthProblemException;
}
