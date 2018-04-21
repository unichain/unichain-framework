package tech.unichain.framework.core.dict.apply;

/**
 * 数据字典应用类
 *
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictApply {

    <T> T apply(T bean);

}
