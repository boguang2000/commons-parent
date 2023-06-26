package cn.aotcloud.oauth2.altu.oauth2.rs;

import cn.aotcloud.oauth2.altu.oauth2.common.OAuth;
import cn.aotcloud.oauth2.altu.oauth2.common.error.OAuthError;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthProblemException;
import cn.aotcloud.oauth2.altu.oauth2.common.exception.OAuthSystemException;
import cn.aotcloud.oauth2.altu.oauth2.common.message.OAuthResponse;
import cn.aotcloud.oauth2.altu.oauth2.common.utils.OAuthUtils;
import cn.aotcloud.oauth2.altu.oauth2.rs.response.OAuthRSResponse;
import cn.aotcloud.utils.HttpServletUtil;

import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理OAuth发生的异常 {@link OAuthProblemException}，生成合适的 {@link OAuthResponse}。
 * 
 * @author xkxu
 *
 */
public class OAuthExceptionHandler {

	public void handle(HttpServletResponse resp, 
			OAuthProblemException error) throws OAuthSystemException, IOException {
		
		OAuthResponse oauthResponse = handle(error).buildJSONMessage();
            
		HttpServletUtil.addHeader(resp, OAuth.HeaderType.WWW_AUTHENTICATE,
            oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
        
		HttpServletUtil.setStatus(resp, oauthResponse.getResponseStatus());
        
		HttpServletUtil.setContentType(resp, OAuth.ContentType.JSON);
        
        FileCopyUtils.copy(oauthResponse.getBody(), HttpServletUtil.getPrintWriter(resp));
	}
	
	public OAuthResponse.OAuthErrorResponseBuilder handle(OAuthProblemException error) throws OAuthSystemException {
		OAuthResponse.OAuthErrorResponseBuilder oauthResponse = null;
        if (OAuthUtils.isEmpty(error.getError())) {
            oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            int responseCode = 401;
            if (error.getError().equals(OAuthError.ResourceResponse.INSUFFICIENT_SCOPE)
            		|| error.getError().equals(OAuthError.CodeResponse.ACCESS_DENIED)) {
                responseCode = 403;
            }

            oauthResponse = OAuthRSResponse
                .errorResponse(responseCode)
                //.setRealm(realm)
                .setError(error.getError())
                .setErrorDescription(error.getDescription())
                .setErrorUri(error.getUri());
        }
        return oauthResponse;
        
	}
}
