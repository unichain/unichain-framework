package tech.unichain.framework.orm.core;


import tech.unichain.framework.orm.core.meta.TableMetaData;

public interface ObjectWrapperFactory {
    <T> ObjectWrapper<T> createObjectWrapper(TableMetaData metaData);
}
