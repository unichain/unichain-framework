package tech.unichain.framework.orm.rdb.render;

import tech.unichain.framework.orm.rdb.executor.SQL;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.render.support.simple.SimpleSQL;

/**
 * SQL渲染器
 *
 * @param <R> 参数泛型
 */
public interface SqlRender<R> {

    /**
     * 根据表结构对象渲染SQL
     *
     * @param metaData 表结构对象 {@link RDBTableMetaData}
     * @param param    渲染参数
     * @return 渲染结果
     * @see {@link SimpleSQL}
     */
    SQL render(RDBTableMetaData metaData, R param);

    enum TYPE {
        INSERT, DELETE, UPDATE, SELECT, SELECT_TOTAL, META_ALTER, META_CREATE, META_DROP
    }
}
