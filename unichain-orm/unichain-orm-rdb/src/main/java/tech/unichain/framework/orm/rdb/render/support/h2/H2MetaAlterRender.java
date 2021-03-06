package tech.unichain.framework.orm.rdb.render.support.h2;

import tech.unichain.framework.orm.rdb.executor.EmptySQL;
import tech.unichain.framework.orm.rdb.meta.RDBColumnMetaData;
import tech.unichain.framework.orm.rdb.meta.RDBDatabaseMetaData;
import tech.unichain.framework.orm.rdb.render.support.simple.SimpleSQL;
import org.hswebframework.utils.StringUtils;
import tech.unichain.framework.orm.rdb.executor.BindSQL;
import tech.unichain.framework.orm.rdb.executor.SQL;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.render.SqlAppender;
import tech.unichain.framework.orm.rdb.render.SqlRender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by lait.zhang@gmail.com on 16-6-5.
 */
public class H2MetaAlterRender implements SqlRender<Boolean> {

    private RDBDatabaseMetaData databaseMetaData;

    public H2MetaAlterRender(RDBDatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public SQL render(RDBTableMetaData metaData, Boolean executeRemove) {
        RDBTableMetaData old = databaseMetaData.getTableMetaData(metaData.getName());
        if (old == null) throw new UnsupportedOperationException("旧表不存在!");
        List<RDBColumnMetaData> changedField = new ArrayList<>();
        List<RDBColumnMetaData> addedField = new ArrayList<>();
        List<RDBColumnMetaData> deletedField = new ArrayList<>();

        RDBTableMetaData oldMeta = old;
        if (executeRemove)
            oldMeta.getColumns().forEach(oldField -> {
                RDBColumnMetaData newMeta = metaData.findColumn(oldField.getName());
                if (newMeta == null) {
                    newMeta = metaData.getColumns().stream()
                            .filter(fieldMetaData -> oldField.getName().equals(fieldMetaData.getProperty("old-name").getValue()))
                            .findFirst().orElse(null);
                }
                if (newMeta == null || !newMeta.getName().equals(oldField.getName())) {
                    //删除的字段
                    deletedField.add(oldField);
                }
            });
        metaData.getColumns().forEach(newField -> {
            String oldName = newField.getProperty("old-name", newField.getName()).getValue();
            RDBColumnMetaData oldField = oldMeta.findColumn(oldName);
            if (oldField == null) {
                //增加的字段
                addedField.add(newField);
            } else {
                if (!newField.getName().equals(oldField.getName()) ||
                        !newField.getDataType().equals(oldField.getDataType())
                        || !newField.getComment().equals(oldField.getComment())
                        || oldField.isNotNull() != newField.isNotNull()) {
                    changedField.add(newField);
                }
            }
        });
        LinkedList<BindSQL> bind = new LinkedList<>();
        List<String> comments = new ArrayList<>();
        String newTableComment = metaData.getComment();
        String oldTableComment = old.getComment();
        if (newTableComment == null) newTableComment = "";
        if (oldTableComment == null) oldTableComment = "";
        if (!newTableComment.equals(oldTableComment)) {
            comments.add(String.format("COMMENT ON TABLE %s IS '%s'", metaData.getName(), metaData.getComment()));
        }
        if (addedField.isEmpty() && changedField.isEmpty() && deletedField.isEmpty() && comments.isEmpty()) {
            return new EmptySQL();
        }
        addedField.forEach(column -> {
            SqlAppender append = new SqlAppender();
            append.add("ALTER TABLE ", metaData.getName(), " ADD ", column.getName(), " ", column.getDataType());
            if (column.isNotNull() || column.isPrimaryKey()) {
                append.add(" NOT NULL");
            }
            if (column.isPrimaryKey()) {
                append.add(" PRIMARY KEY");
            }
            if (StringUtils.isNullOrEmpty(column.getComment())) {
//                comments.add(String.format("COMMENT ON COLUMN %s.%s is '%s'", metaData.getName(), column.getName(), column.getAlias()));
            } else {
                comments.add(String.format("COMMENT ON COLUMN %s.%s is '%s'", metaData.getName(), column.getName(), column.getComment()));
            }
            SimpleSQL simpleSQL = new SimpleSQL(append.toString(), column);
            BindSQL bindSQL = new BindSQL();
            bindSQL.setSql(simpleSQL);
            bindSQL.setToField(column.getName());
            bind.add(bindSQL);
        });
        changedField.forEach(column -> {
            String oldName = column.getProperty("old-name").getValue();
            if (oldName == null) oldName = column.getName();
            RDBColumnMetaData oldField = oldMeta.findColumn(oldName);
            if (!oldName.equals(column.getName())) {
                SqlAppender renameSql = new SqlAppender();
                renameSql.add("ALTER TABLE ", metaData.getName(), " ALTER COLUMN ", oldName, " RENAME TO ", column.getName());
                BindSQL bindSQL = new BindSQL();
                bindSQL.setSql(new SimpleSQL(renameSql.toString()));
                bind.add(bindSQL);
                metaData.renameColumn(oldName, column.getName());
            }
            if (!oldField.getDataType().equals(column.getDataType())
                    || oldField.isNotNull() != column.isNotNull()) {
                SqlAppender append = new SqlAppender();
                append.add("ALTER TABLE ", metaData.getName(), " MODIFY ", column.getName(), " ", column.getDataType());
                if (column.isNotNull()) {
                    append.add(" NOT NULL");
                } else {
                    append.add(" NULL");
                }
                if (column.isPrimaryKey()) append.add(" PRIMARY KEY");

                SimpleSQL simpleSQL = new SimpleSQL(append.toString(), column);
                BindSQL bindSQL = new BindSQL();
                bindSQL.setSql(simpleSQL);
                bindSQL.setToField(column.getName());
                bind.add(bindSQL);
            }
            String nc = column.getComment();
            String oc = oldField.getComment();
            if (nc == null) nc = "";
            if (oc == null) oc = "";
            if (nc.equals(oc)) return;
            if (StringUtils.isNullOrEmpty(nc)) {
                //   comments.add(String.format("COMMENT ON COLUMN %s.%s IS '新建字段:%s'", metaData.getName(), column.getName(), column.getAlias()));
            } else {
                comments.add(String.format("COMMENT ON COLUMN %s.%s IS '%s'", metaData.getName(), column.getName(), nc));
            }
        });
        deletedField.forEach(field -> {
            String dropSql = String.format("ALTER TABLE %s DROP COLUMN %s", metaData.getName(), field.getName());
            SimpleSQL simpleSQL = new SimpleSQL(dropSql, field);
            BindSQL bindSQL = new BindSQL();
            bindSQL.setSql(simpleSQL);
            bindSQL.setToField(field.getName());
            bind.add(bindSQL);
        });
        LinkedList<BindSQL> commentSql = new LinkedList<>(comments.stream().map(s -> {
            BindSQL binSql = new BindSQL();
            binSql.setSql(new SimpleSQL(s, s));
            return binSql;
        }).collect(Collectors.toList()));

        SQL sql = null;
        bind.addAll(commentSql);
        if (bind.isEmpty()) {
            return new EmptySQL();
        }
        if (!bind.isEmpty()) {
            sql = bind.get(0).getSql();
            bind.removeFirst();
        }
        if (sql != null && !bind.isEmpty())
            ((SimpleSQL) sql).setBindSQLs(bind);
        return sql;
    }

}
