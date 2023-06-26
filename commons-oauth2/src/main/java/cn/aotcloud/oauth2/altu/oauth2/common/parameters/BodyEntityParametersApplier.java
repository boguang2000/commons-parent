package cn.aotcloud.oauth2.altu.oauth2.common.parameters;


import java.util.Map;

import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.oauth2.altu.oauth2.common.message.OAuthMessage;

/**
 * @author xkxu
 */
public class BodyEntityParametersApplier implements OAuthParametersApplier {

	@Override
	public OAuthMessage applyOAuthParameters(
			OAuthMessage message, Map<String, Object> params) throws OAuthSystemException {
		message.setBodyEntity(params);
		return message;
	}

}
