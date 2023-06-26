package cn.aotcloud.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xkxu
 */
public interface LoginFailStrategy {

	/**
	 * @param request
	 * @param response
	 * @param e
	 * @throws IOException
	 */
	public void onFail(HttpServletRequest request, 
			HttpServletResponse response, Exception e) throws IOException;
}
