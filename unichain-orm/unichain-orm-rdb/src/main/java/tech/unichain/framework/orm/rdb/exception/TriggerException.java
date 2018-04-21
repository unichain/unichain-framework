package tech.unichain.framework.orm.rdb.exception;

/**
 * 触发器异常,继承自{@link RuntimeException},在执行触发器时发生错误将抛出此异常
 *
 * @author lait.zhang@gmail.com
 * @since 1.0
 */
public class TriggerException extends RuntimeException {
    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TriggerException(String message) {
        super(message);
    }
}
