package tech.unichain.framework.orm.rdb.meta.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.hswebframework.ezorm.core.ValueConverter;

public class JSONValueConverter implements ValueConverter {
    private Class type;

    private ValueConverter parent;

    public JSONValueConverter(Class type) {
        this(type, null);
    }

    public JSONValueConverter(Class type, ValueConverter parent) {
        this.type = type;
        this.parent = parent;
    }

    @Override
    public Object getData(Object value) {
        return JSON.toJSONString(parent == null ? value : parent.getValue(value), SerializerFeature.WriteClassName);
    }

    @Override
    public Object getValue(Object data) {
        if (parent != null) data = parent.getValue(data);
        if (data instanceof String) {
            String str = (String) data;
            if (str.startsWith("[")) {
                return JSON.parseArray(str, type);
            }
            return JSON.parseObject(str, type);
        }
        return data;
    }
}
