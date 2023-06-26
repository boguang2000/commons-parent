package cn.aotcloud.oauth2.altu.oauth2.rs.validator;

import javax.servlet.http.HttpServletRequest;

import cn.aotcloud.oauth2.altu.oauth2.common.OAuth;
import cn.aotcloud.oauth2.altu.oauth2.common.error.OAuthError;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;
import cn.aotcloud.oauth2.altu.oauth2.common.validators.AbstractValidator;


/**
 * 
 * @author xkxu
 *
 */
public class BearerCookieOAuthValidator extends AbstractValidator<HttpServletRequest> {

	@Override
    public void validateContentType(HttpServletRequest request) throws OAuthProblemException {
    }

    @Override
    public void validateMethod(HttpServletRequest request) throws OAuthProblemException {
    }
    
	@Override
	public void validateRequiredParameters(HttpServletRequest request) throws OAuthProblemException {
		String value = OAuthUtils.getCookieValue(request, OAuth.Cookie.COOKIE_NAME);
		
		if (OAuthUtils.isEmpty(value)) {
            throw OAuthProblemException.error(
            		OAuthError.ResourceResponse.INVALID_REQUEST, "Missing access Token Cookie.");
        }
	}
}
