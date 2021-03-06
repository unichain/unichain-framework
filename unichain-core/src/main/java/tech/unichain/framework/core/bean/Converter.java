package tech.unichain.framework.core.bean;

@FunctionalInterface
public interface Converter {
    <T> T convert(Object source, Class<T> targetClass,Class[] genericType);
}