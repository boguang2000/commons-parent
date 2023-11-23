package cn.aotcloud.security.oncetoken.support;

import cn.aotcloud.exception.BaseExceptionEmpty;
import cn.aotcloud.exception.ExceptionUtil;
import cn.aotcloud.security.oncetoken.IllegalRequestTokenException;
import cn.aotcloud.security.oncetoken.OnceProtocol;
import cn.aotcloud.security.oncetoken.RequestToken;
import cn.aotcloud.security.oncetoken.RequestTokenHandler;
import cn.aotcloud.security.oncetoken.RequestTokenParser;
import cn.aotcloud.security.oncetoken.RequestTokenStore;
import cn.aotcloud.security.oncetoken.RequestTokenValidator;
import cn.aotcloud.security.oncetoken.event.IllegalRequestTokenApplicationEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于Timestamp和Nonce的方案。
 *
 * @author xkxu
 *
 * https://blog.csdn.net/koastal/article/details/53456696
 */
public class DefaultRequestTokenHandler implements RequestTokenHandler, ApplicationEventPublisherAware {

	//private LoggerHandle logger = LoggerHandle.Instance(getClass());

	private RequestTokenStore requestTokenStore;

	private ApplicationEventPublisher applicationEventPublisher;

	private RequestTokenParser requestTokenParser;

	private RequestTokenValidator requestTokenValidator;
	
	private List<OnceProtocol> supportedProtocols;
	
	public DefaultRequestTokenHandler(RequestTokenStore requestTokenStore, RequestTokenValidator requestTokenValidator) {
		this.requestTokenStore = requestTokenStore;
		this.requestTokenValidator = requestTokenValidator;
		requestTokenParser = new DelegateRequestTokenParser(supportedProtocols);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void validate(HttpServletRequest request) throws IllegalRequestTokenException {
		RequestToken requestTokenFromRequest = parseRequestToken(request);
		try {
			requestTokenValidator.validate(requestTokenFromRequest);
			requestTokenStore.save(requestTokenFromRequest);
		} catch(IllegalRequestTokenException e) {
			applicationEventPublisher.publishEvent(new IllegalRequestTokenApplicationEvent(requestTokenFromRequest != null ? requestTokenFromRequest : RequestToken.ILLEGAL_REQUEST_TOKEN));
			throw new BaseExceptionEmpty(ExceptionUtil.getMessage(e));
		}
	}

	protected RequestToken parseRequestToken(HttpServletRequest request) {
		return requestTokenParser.parse(request);
	}

}
