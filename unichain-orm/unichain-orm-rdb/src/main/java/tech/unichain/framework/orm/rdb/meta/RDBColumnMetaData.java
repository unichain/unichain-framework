package tech.unichain.framework.orm.rdb.meta;

import org.hswebframework.ezorm.core.ValueConverter;
import org.hswebframework.ezorm.core.meta.AbstractColumnMetaData;
import org.hswebframework.ezorm.core.meta.ColumnMetaData;
import tech.unichain.framework.orm.rdb.meta.converter.DefaultValueConverter;

import java.io.Serializable;
import java.sql.JDBCType;

public class RDBColumnMetaData extends AbstractColumnMetaData implements ColumnMetaData, Serializable, Cloneable, Comparable<RDBColumnMetaData> {
    private static final DefaultValueConverter DEFAULT_VALUE_CONVERTER = new DefaultValueConverter();

    public RDBColumnMetaData() {
    }

    public RDBColumnMetaData(String name, Class javaType, String dataType, JDBCType jdbcType) {
        this.name = name;
        this.javaType = javaType;
        this.dataType = dataType;
        this.jdbcType = jdbcType;
    }

    private String dataType;

    /**
     * 长度
     *
     * @since 1.1
     */
    private int length;

    /**
     * 精度
     *
     * @since 1.1
     */
    private int precision;

    /**
     * 小数位数
     *
     * @since 1.1
     */
    private int scale;

    /**
     * 是否不能为空
     */
    private boolean notNull;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    private JDBCType jdbcType;

    private int sortIndex;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public RDBTableMetaData getTableMetaData() {
        return super.getTableMetaData();
    }

    public String getFullName() {
        return tableMetaData == null ? getName() : tableMetaData.getName() + "." + getName();
    }

    public String getFullAliasName() {
        return tableMetaData == null ? getAlias() : tableMetaData.getAlias() + "." + getAlias();
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    @Override
    public ValueConverter getValueConverter() {
        if (valueConverter == null) valueConverter = DEFAULT_VALUE_CONVERTER;
        return super.getValueConverter();
    }

    @Override
    public int compareTo(RDBColumnMetaData o) {
        return Integer.compare(sortIndex, o.getSortIndex());
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isNotNull() {
        if (!notNull && isPrimaryKey()) notNull = true;
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public RDBColumnMetaData clone() {
        RDBColumnMetaData target = new RDBColumnMetaData();
        target.name = name;
        target.alias = alias;
        target.comment = comment;
        target.javaType = javaType;
        target.jdbcType = jdbcType;
        target.dataType = dataType;
        target.properties = properties;
        target.optionConverter = optionConverter;
        target.tableMetaData = tableMetaData;
        target.sortIndex = sortIndex;
        target.length = length;
        target.scale = scale;
        target.precision = precision;
        target.notNull = notNull;
        target.primaryKey = primaryKey;
        target.defaultValue=defaultValue;
        target.valueConverter=valueConverter;
        return target;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", comment='" + comment + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
