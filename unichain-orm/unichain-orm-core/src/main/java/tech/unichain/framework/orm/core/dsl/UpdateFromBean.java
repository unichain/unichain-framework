package tech.unichain.framework.orm.core.dsl;

import tech.unichain.framework.orm.core.ConditionalFromBean;
import tech.unichain.framework.orm.core.NestConditionalFromBean;
import tech.unichain.framework.orm.core.param.UpdateParam;

/**
 * @author lait.zhang@gmail.com
 */
public final class UpdateFromBean<T, Q extends UpdateParam<T>>
        implements ConditionalFromBean<UpdateFromBean<T, Q>> {
    private Accepter<UpdateFromBean<T, Q>, Object> accepter = this::and;
    private Update<T, Q>                                                      proxy    = null;

    public UpdateFromBean(Update<T, Q> proxy) {
        this.proxy = proxy;
    }

    @Override
    public Object getBean() {
        return proxy.getBean();
    }

    public Update<T, Q> fromCustom() {
        return proxy;
    }

    @Override
    public UpdateFromBean<T, Q> sql(String sql, Object... params) {
        proxy.sql(sql, params);
        return this;
    }

    @Override
    public NestConditionalFromBean<UpdateFromBean<T, Q>> nest() {
        return new SimpleNestConditionalForBean<>(this, proxy.getParam().nest());
    }

    @Override
    public NestConditionalFromBean<UpdateFromBean<T, Q>> nest(String column) {
        return new SimpleNestConditionalForBean<>(this, proxy.getParam().nest(column, getValue(column)));
    }

    @Override
    public NestConditionalFromBean<UpdateFromBean<T, Q>> orNest() {
        return new SimpleNestConditionalForBean<>(this, proxy.getParam().orNest());
    }

    @Override
    public NestConditionalFromBean<UpdateFromBean<T, Q>> orNest(String column) {
        return new SimpleNestConditionalForBean<>(this, proxy.getParam().orNest(column, getValue(column)));
    }

    @Override
    public UpdateFromBean<T, Q> and() {
        proxy.and();
        return this;
    }

    @Override
    public UpdateFromBean<T, Q> or() {
        proxy.or();
        return this;
    }

    public UpdateFromBean<T, Q> and(String column, String termType, Object value) {
        proxy.and(column, termType, value);
        return this;
    }

    public UpdateFromBean<T, Q> or(String column, String termType, Object value) {
        proxy.or(column, termType, value);
        return this;
    }

    @Override
    public UpdateFromBean<T, Q> and(String column, String termType) {
        and(column, termType, getValue(column));
        return this;
    }

    @Override
    public UpdateFromBean<T, Q> or(String column, String termType) {
        or(column, termType, getValue(column));
        return this;
    }

    @Override
    public Accepter<UpdateFromBean<T, Q>, Object> getAccepter() {
        return accepter;
    }

    public UpdateFromBean<T, Q> excludes(String... columns) {
        proxy.excludes(columns);
        return this;
    }

    public UpdateFromBean<T, Q> includes(String... columns) {
        proxy.includes(columns);
        return this;
    }

    public int exec() {
        return proxy.exec();
    }

}
