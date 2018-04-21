package tech.unichain.framework.orm.rdb.render.support.oracle;

import tech.unichain.framework.orm.rdb.meta.RDBColumnMetaData;
import org.hswebframework.utils.StringUtils;
import tech.unichain.framework.orm.rdb.executor.BindSQL;
import tech.unichain.framework.orm.rdb.executor.SQL;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.render.SqlAppender;
import tech.unichain.framework.orm.rdb.render.SqlRender;
import tech.unichain.framework.orm.rdb.render.support.simple.SimpleSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OracleMetaCreateRender implements SqlRender<Object> {
    @Override
    public SQL render(RDBTableMetaData metaData, Object param) {
        SqlAppender createBody = new SqlAppender();
        List<String> comments = new ArrayList<>();
        Set<RDBColumnMetaData> RDBColumnMetaDatas = metaData.getColumns();
        if (RDBColumnMetaDatas.isEmpty()) throw new UnsupportedOperationException("未指定任何字段");
        createBody.add("\nCREATE TABLE ", metaData.getName(), "(");
        RDBColumnMetaDatas.forEach(column -> {
            createBody.add("\n\t\"", column.getName().toUpperCase(), "\" ").add(column.getDataType());
            if (column.isNotNull()
                    || column.isPrimaryKey()) {
                createBody.add(" NOT NULL ");
            }
            if (column.isPrimaryKey())
                createBody.add("PRIMARY KEY ");
            //注释
            if (!StringUtils.isNullOrEmpty(column.getComment())) {
                comments.add(String.format("COMMENT ON COLUMN %s.\"%s\" IS '%s'", metaData.getName(), (column.getName().toUpperCase()), column.getComment()));
            } else {
                comments.add(String.format("COMMENT ON COLUMN %s.\"%s\" IS '%s'", metaData.getName(), (column.getName().toUpperCase()), column.getAlias()));

            }
            createBody.add(",");
        });
        comments.add(String.format("COMMENT ON TABLE %s IS '%s'", metaData.getName(), metaData.getComment()));
        createBody.removeLast();
        createBody.add("\n)");
        SimpleSQL simpleSQL = new SimpleSQL(createBody.toString(), param);
        List<BindSQL> bindSQLs = comments.stream().map(s -> {
            BindSQL sql = new BindSQL();
            sql.setSql(new SimpleSQL(s, param));
            return sql;
        }).collect(Collectors.toList());
        simpleSQL.setBindSQLs(bindSQLs);
        return simpleSQL;
    }
}
