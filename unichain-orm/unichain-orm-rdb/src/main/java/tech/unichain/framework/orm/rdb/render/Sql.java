package tech.unichain.framework.orm.rdb.render;

/**
 * TODO 完成注释
 *
 * @author lait.zhang@gmail.com
 */
public interface Sql {
    String getSql();

    static Sql build(String sql) {
        return () -> sql;
    }
}
