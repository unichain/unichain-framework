package tech.unichain.framework.orm.rdb.simple;

import org.hswebframework.ezorm.core.Delete;
import org.hswebframework.ezorm.core.NestConditional;
import org.hswebframework.ezorm.core.SimpleNestConditional;
import org.hswebframework.ezorm.core.Trigger;
import org.hswebframework.ezorm.core.param.Param;
import org.hswebframework.ezorm.core.param.SqlTerm;
import tech.unichain.framework.orm.rdb.executor.SQL;
import tech.unichain.framework.orm.rdb.executor.SqlExecutor;
import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;
import tech.unichain.framework.orm.rdb.render.SqlRender;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by lait.zhang@gmail.com on 16-6-5.
 */
class SimpleDelete extends ValidatorAndTriggerSupport<Delete> implements Delete {
    private Param            param;
    private SimpleTable      table;
    private SqlExecutor      sqlExecutor;
    private RDBTableMetaData tableMetaData;
    private Accepter<Delete,Object> accepter = this::and;

    public SimpleDelete(SimpleTable table, SqlExecutor sqlExecutor) {
        this.table = table;
        this.sqlExecutor = sqlExecutor;
        this.param = new Param();
        this.tableMetaData = table.getMeta();
    }

    @Override
    protected Delete addSqlTerm(SqlTerm term) {
        param.addTerm(term);
        return this;
    }

    @Override
    public Delete and(String condition, String termType, Object value) {
        param.and(condition, termType, value);
        return this;
    }

    @Override
    public Delete or(String condition, String termType, Object value) {
        param.or(condition, termType, value);
        return this;
    }

    @Override
    public Delete and() {
        setAnd();
        accepter = this::and;
        return this;
    }

    @Override
    public Delete or() {
        setOr();
        accepter = this::or;
        return this;
    }

    @Override
    public Accepter<Delete,Object> getAccepter() {
        return accepter;
    }

    @Override
    public NestConditional<Delete> nest() {
        return new SimpleNestConditional<>(this, param.nest());
    }

    @Override
    public NestConditional<Delete> nest(String column, Object value) {
        return new SimpleNestConditional<>(this, param.nest(column, value));
    }

    @Override
    public NestConditional<Delete> orNest() {
        return new SimpleNestConditional<>(this, param.orNest());
    }

    @Override
    public NestConditional<Delete> orNest(String column, Object value) {
        return new SimpleNestConditional<>(this, param.orNest(column, value));
    }

    @Override
    public Delete setParam(Param param) {
        this.param = param;
        return this;
    }

    @Override
    public int exec() throws SQLException {
        Map<String, Object> context = null;
        boolean supportBefore = !triggerSkip && tableMetaData.triggerIsSupport(Trigger.delete_before);
        boolean supportDone = !triggerSkip && tableMetaData.triggerIsSupport(Trigger.delete_done);
        if (supportBefore || supportDone) {
            context = table.getDatabase().getTriggerContextRoot();
            context.put("table", table);
            context.put("database", table.getDatabase());
            context.put("param", param);
        }
        if (supportBefore) {
            tableMetaData.on(Trigger.delete_before, context);
        }
        SqlRender<Param> render = tableMetaData.getDatabaseMetaData().getRenderer(SqlRender.TYPE.DELETE);
        SQL sql = render.render(table.getMeta(), param);
        int size = sqlExecutor.delete(sql);
        if (supportDone) {
            context.put("total", size);
            tableMetaData.on(Trigger.delete_done, context);
        }
        return size;
    }

    @Override
    RDBTableMetaData getTableMeta() {
        return tableMetaData;
    }
}
