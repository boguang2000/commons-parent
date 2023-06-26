package cn.aotcloud.oauth2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xkxu
 */
public interface LogoutSuccessStrategy {
	
	public void onSuccess(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
