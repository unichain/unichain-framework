package tech.unichain.framework.core.convert;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface CustomMessageConverter {
    boolean support(Class clazz);

    Object convert(Class clazz, byte[] message);
}
