package cn.aotcloud.exception;

/**
 * 应用中心异常基类，应用中心所有的异常都应该使用或者继承该类，其他项目的异常也应该继承于该类。
 *
 * 通过异常编码对象 {@link ErrorCode} 创建该异常。
 *
 * @author xkxu
 *
 * @see com.sgitg.app.core.exception.ErrorCode
 */
public class BaseExceptionEmpty extends BaseException {

    private static final long serialVersionUID = 5713414039581184812L;

    public BaseExceptionEmpty() {
        super();
    }

    public BaseExceptionEmpty(String message) {
        super(message);
    }

    public BaseExceptionEmpty(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
