package cn.aotcloud.exception;

public class SafeException extends BaseException {

	private static final long serialVersionUID = 1L;


	public SafeException(String message) {
		super(message, "SAFE");
	}

	public SafeException(String message, Throwable cause) {
		super(message, cause, "SAFE");
	}

	public SafeException(Throwable cause) {
		super(cause, "SAFE");
	}
}
