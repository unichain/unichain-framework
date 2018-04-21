package tech.unichain.framework.orm.rdb;

import org.hswebframework.ezorm.core.Table;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;

public interface RDBTable<T> extends Table<T> {
    RDBTableMetaData getMeta();

    RDBQuery<T> createQuery();

}
