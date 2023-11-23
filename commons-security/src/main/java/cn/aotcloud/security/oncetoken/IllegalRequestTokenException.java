package cn.aotcloud.security.oncetoken;

import cn.aotcloud.exception.BaseExceptionEmpty;

/**
 * @author xkxu
 */
public class IllegalRequestTokenException extends BaseExceptionEmpty {
	
	private static final long serialVersionUID = 1L;

	public IllegalRequestTokenException() {
		super("防重放拦截：重放攻击");
	}
	
	public IllegalRequestTokenException(String message) {
		super(message);
	}
}
