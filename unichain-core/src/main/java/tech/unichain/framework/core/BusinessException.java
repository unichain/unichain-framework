package tech.unichain.framework.core;

/**
 * 业务异常
 *
 * @author lait.zhang
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 5441923856899380112L;

    private int status = 500;

    public BusinessException(String message) {
        this(message, 500);
    }

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
