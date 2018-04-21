package tech.unichain.framework.orm.rdb.exception;

/**
 * 验证器异常,在对表数据操作前将对数据进行验证,如果验证失败,将抛出此异常
 *
 * @author lait.zhang@gmail.com
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {
    /**
     * 验证结果,捕获此异常后,可获取此对象的到验证结果
     */
    private Object validateResult;

    public ValidationException(String message, Object validateResult) {
        super(message);
        this.validateResult = validateResult;
    }

    public Object getValidateResult() {
        return validateResult;
    }
}
