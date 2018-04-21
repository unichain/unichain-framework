package tech.unichain.framework.orm.rdb.render.support.simple;

import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.executor.SQL;
import org.hswebframework.ezorm.core.param.Param;
import org.hswebframework.ezorm.core.param.Term;
import tech.unichain.framework.orm.rdb.render.dialect.Dialect;
import tech.unichain.framework.orm.rdb.render.SqlAppender;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleDeleteSqlRender extends CommonSqlRender<Param> {
    public SimpleDeleteSqlRender(Dialect dialect) {
        setDialect(dialect);
    }

    class SimpleDeleteSqlRenderProcess extends SimpleWhereSqlBuilder {
        private RDBTableMetaData metaData;
        private Param            param;
        private SqlAppender whereSql = new SqlAppender();

        public SimpleDeleteSqlRenderProcess(RDBTableMetaData metaData, Param param) {
            this.metaData = metaData;
            this.param = param;
            List<Term> terms = param.getTerms();
            terms = terms.stream().filter(term -> term.getColumn()==null||!term.getColumn().contains(".")).collect(Collectors.toList());
            param.setTerms(terms);
            //解析查询条件
            buildWhere(metaData, "", terms, whereSql, new HashSet<>());
            if (!whereSql.isEmpty()) whereSql.removeFirst();
        }

        public SQL process() {
            SqlAppender appender = new SqlAppender();
            appender.add("DELETE FROM ", metaData.getName()," ",metaData.getAlias());
            if (whereSql.isEmpty()) {
                throw new UnsupportedOperationException("禁止执行未设置任何条件的删除操作!");
            }
            appender.add(" WHERE", " ").addAll(whereSql);
            String sql = appender.toString();
            SimpleSQL simpleSQL = new SimpleSQL( sql, param);
            return simpleSQL;
        }

        @Override
        public Dialect getDialect() {
            return dialect;
        }
    }

    @Override
    public SQL render(RDBTableMetaData metaData, Param param) {
        return new SimpleDeleteSqlRenderProcess(metaData, param).process();
    }

    private Dialect dialect;

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}
