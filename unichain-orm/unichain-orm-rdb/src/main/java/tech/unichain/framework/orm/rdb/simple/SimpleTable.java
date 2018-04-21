package tech.unichain.framework.orm.rdb.simple;

import org.hswebframework.ezorm.core.Delete;
import org.hswebframework.ezorm.core.Insert;
import org.hswebframework.ezorm.core.ObjectWrapper;
import org.hswebframework.ezorm.core.Update;
import tech.unichain.framework.orm.rdb.executor.SqlExecutor;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.RDBQuery;
import tech.unichain.framework.orm.rdb.RDBTable;

/**
 * Created by lait.zhang@gmail.com on 16-6-4.
 */
class SimpleTable<T> implements RDBTable<T> {
    private RDBTableMetaData metaData;

    private SqlExecutor sqlExecutor;

    private ObjectWrapper objectWrapper;

    private SimpleDatabase database;

    public SimpleTable(RDBTableMetaData metaData, SimpleDatabase database, SqlExecutor sqlExecutor, ObjectWrapper objectWrapper) {
        this.metaData = metaData;
        this.sqlExecutor = sqlExecutor;
        this.objectWrapper = objectWrapper;
        this.database = database;
    }

    @Override
    public RDBTableMetaData getMeta() {
        return metaData;
    }

    @Override
    public RDBQuery createQuery() {
        return new SimpleQuery<>(this, sqlExecutor, objectWrapper);
    }

    @Override
    public Update createUpdate() {
        return new SimpleUpdate<>(this, sqlExecutor);
    }

    @Override
    public Delete createDelete() {
        return new SimpleDelete(this, sqlExecutor);
    }

    @Override
    public Insert<T> createInsert() {
        return new SimpleInsert<>(this, sqlExecutor);
    }

    public SimpleDatabase getDatabase() {
        return database;
    }
}
