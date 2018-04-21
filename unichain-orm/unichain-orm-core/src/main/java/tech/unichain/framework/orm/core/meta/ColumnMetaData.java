package tech.unichain.framework.orm.core.meta;


import tech.unichain.framework.orm.core.DefaultValue;
import tech.unichain.framework.orm.core.OptionConverter;
import tech.unichain.framework.orm.core.PropertyWrapper;
import tech.unichain.framework.orm.core.ValueConverter;

import java.io.Serializable;
import java.util.Set;

public interface ColumnMetaData extends Serializable, Cloneable {
    String getName();

    String getAlias();

    String getComment();

    Class getJavaType();

    <T extends TableMetaData> T getTableMetaData();

    ValueConverter getValueConverter();

    OptionConverter getOptionConverter();

    DefaultValue getDefaultValue();

    Set<String> getValidator();

    PropertyWrapper getProperty(String property);

    PropertyWrapper getProperty(String property, Object defaultValue);

    PropertyWrapper setProperty(String property, Object value);

    <T extends ColumnMetaData> T clone();
}
