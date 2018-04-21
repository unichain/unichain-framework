package tech.unichain.framework.orm.rdb.meta.converter;


import org.hswebframework.ezorm.core.ValueConverter;

/**
 * Created by lait.zhang@gmail.com on 16-6-4.
 */
public class DefaultValueConverter implements ValueConverter {
    @Override
    public Object getData(Object value) {
        return value;
    }

    @Override
    public Object getValue(Object data) {
        return data;
    }
}
