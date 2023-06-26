package cn.aotcloud.oauth2.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

import cn.aotcloud.logger.LoggerHandle;
import cn.aotcloud.smcrypto.Sm4Utils;
import cn.aotcloud.smcrypto.exception.InvalidCryptoDataException;
import cn.aotcloud.smcrypto.exception.InvalidKeyException;

/**
 * 基于OAuth2.0协议的登录拦截过滤器实现,支持会话注销功能
 * 
 * @author xkxu
 */
public abstract class BaseAuthenticationFilter extends OncePerRequestFilter {

	protected static LoggerHandle logger = new LoggerHandle(BaseAuthenticationFilter.class);
			
	public BaseAuthenticationFilter() {
		
	}

	@Override
	protected String getAlreadyFilteredAttributeName() {
		return BaseAuthenticationFilter.class.getName() + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX;
	}

	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(request, response);
	}

	public static String getAccessToken(HttpServletRequest request, String accessTokenAttrName) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (String) session.getAttribute(accessTokenAttrName);
		}
		return null;
	}
	
	public static String getName(String encryptHex) {
		String decryptText = "未知登录认证";
		try {
			decryptText = Sm4Utils.ECB.decryptToText(encryptHex, "dc174a9a612f06107738f8d5b702c2fc");
		} catch (InvalidCryptoDataException e) {
			logger.error("解密AuthType时发生InvalidCryptoDataException异常");
		} catch (InvalidKeyException e) {
			logger.error("解密AuthType时发生InvalidKeyException异常");
		}
		return decryptText;
	}
	
	public enum AuthType {
		
		ISC_SSO(1, getName("8272E04F41E5053BA5D9FB9A3A2F7FD915A902A2651AF4AC3D60EABBD2ADDB62")), 
		ISC_AUTHAPI(2, getName("4B6CCF9C464718B43335C25A99BB6EE8FA7B3A925FD7366E2DD80FDEE0A48CF7")),
		WX_QRCODE(3, getName("AE1B99E2FD391D50DFB32ADFCE699BB498D7D0927A1487E143009E4E979AAC17")), 
		PL_QRCODE(4, getName("6A2E0559E8F56C329928333D9E23564A98D7D0927A1487E143009E4E979AAC17"));

		private final int type;

		private final String desc;

		private AuthType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
	}
}
