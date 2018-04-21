package tech.unichain.framework.orm.rdb.render.support.mysql;

import tech.unichain.framework.orm.rdb.meta.RDBColumnMetaData;
import org.hswebframework.utils.StringUtils;
import tech.unichain.framework.orm.rdb.executor.SQL;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.render.SqlAppender;
import tech.unichain.framework.orm.rdb.render.SqlRender;
import tech.unichain.framework.orm.rdb.render.support.simple.SimpleSQL;

import java.util.Set;

public class MysqlMetaCreateRender implements SqlRender {

    private String engine="InnoDB";

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngine() {
        return engine;
    }

    public MysqlMetaCreateRender() {
    }

    public MysqlMetaCreateRender(String engine) {
        this.engine = engine;
    }

    @Override
    public SQL render(RDBTableMetaData metaData, Object param) {
        SqlAppender appender = new SqlAppender();
        Set<RDBColumnMetaData> RDBColumnMetaDatas = metaData.getColumns();
        if (RDBColumnMetaDatas.isEmpty()) throw new UnsupportedOperationException("未指定任何字段");
        appender.add("\nCREATE TABLE ", metaData.getName(), "(");
        RDBColumnMetaDatas.forEach(fieldMetaData -> {
            appender.add("\n\t`", fieldMetaData.getName(), "` ").add(fieldMetaData.getDataType());
            if (fieldMetaData.isNotNull()) {
                appender.add(" not null");
            }
            if (fieldMetaData.isPrimaryKey()) {
                appender.add(" primary key");
            }
            //注释
            if (!StringUtils.isNullOrEmpty(fieldMetaData.getComment())) {
                appender.add(String.format(" COMMENT '%s'", fieldMetaData.getComment()));
            }
            appender.add(",");
        });
        appender.removeLast();
        appender.add("\n)ENGINE = "+getEngine()+" CHARACTER SET utf8 ");
        if (metaData.getComment() != null) {
            appender.add("COMMENT=", "'", metaData.getComment(), "'");
        }
        return new SimpleSQL(appender.toString(), param);
    }
}
