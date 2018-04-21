package tech.unichain.framework.orm.core;

import tech.unichain.framework.orm.core.meta.TableMetaData;

public interface Table<T> {
    <M extends TableMetaData> M getMeta();

    <Q extends Query<T>> Q createQuery();

    <U extends Update<T>> U createUpdate();

    <I extends Insert<T>> I createInsert();

    <D extends Delete> D createDelete();
}
