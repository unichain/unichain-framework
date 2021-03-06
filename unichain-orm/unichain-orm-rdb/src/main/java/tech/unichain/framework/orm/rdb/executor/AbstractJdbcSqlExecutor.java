package tech.unichain.framework.orm.rdb.executor;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import tech.unichain.framework.orm.rdb.render.support.simple.SimpleSQL;
import org.hswebframework.utils.StringUtils;
import org.hswebframework.ezorm.core.ObjectWrapper;
import org.hswebframework.ezorm.core.param.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC 通用sql执行器,用于执行sql.支持参数化预编译
 *
 * @author lait.zhang@gmail.com
 * @since 1.0.0
 */
public abstract class AbstractJdbcSqlExecutor implements SqlExecutor {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取jdbc链接,由子类实现
     *
     * @return jdbc 链接
     */
    public abstract Connection getConnection();

    /**
     * 直接拼接sql的编译表达式: ${}
     *
     * @since 1.0
     */
    public static final Pattern APPEND_PATTERN = Pattern.compile("(?<=\\$\\{)(.+?)(?=\\})");

    /**
     * 进行参数预编译的表达式:#{}
     *
     * @since 1.0
     */
    public static final Pattern PREPARED_PATTERN = Pattern.compile("(?<=#\\{)(.+?)(?=\\})");

    /**
     * 对象属性操作工具
     *
     * @see PropertyUtilsBean
     */
    protected PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();

    protected Object getSqlParamValue(Object param, String paramName) {
        try {
            Object obj = propertyUtils.getProperty(param, paramName);
            if (obj instanceof Term)
                obj = ((Term) obj).getValue();
            return obj;
        } catch (Exception e) {
            logger.warn("获取sql参数失败", e);
        }
        return null;
    }

    /**
     * 将sql模板编译为sql信息
     * 模板语法:${}代表直接拼接sql,#{}使用预编译
     * 如: 模板参数为:{name:"张三",age:10},sql为:select * from user where name=#{name} and age=${age}
     * 将被编译为:select * from user where name=? and age=10。 参数列表:["张三"]
     *
     * @param sql sql模板 ,参考{@link SimpleSQL}
     * @return sql 编译好的信息
     */
    public SQLInfo compileSql(SQL sql) {
        String sqlTemplate = sql.getSql();
        try {
            SQLInfo sqlInfo = new SQLInfo();
            Object param = sql.getParams();
            Matcher prepared_matcher = PREPARED_PATTERN.matcher(sqlTemplate);
            Matcher append_matcher = APPEND_PATTERN.matcher(sqlTemplate);
            List<Object> params = new LinkedList<>();
            //直接拼接sql
            while (append_matcher.find()) {
                String group = append_matcher.group();
                Object obj = getSqlParamValue(param, group);
                sqlTemplate = sqlTemplate.replaceFirst(StringUtils.concat("\\$\\{", escapeExprSpecialWord(group), "\\}"), String.valueOf(obj));
            }
            //参数预编译sql
            while (prepared_matcher.find()) {
                String group = prepared_matcher.group();
                sqlTemplate = sqlTemplate.replaceFirst(StringUtils.concat("#\\{", escapeExprSpecialWord(group), "\\}"), "?");
                Object obj = getSqlParamValue(param, group);
                params.add(obj);
            }
            sqlInfo.setSql(sqlTemplate);
            sqlInfo.setParam(params.toArray());
            return sqlInfo;
        } catch (Exception e) {
            logger.error("compile sql  {}  error", sqlTemplate, e);
            throw e;
        }
    }

    protected List<SQLInfo> compileAllSql(SQL sql) {
        List<SQLInfo> allSql = new ArrayList<>(4);
        allSql.add(compileSql(sql));
        if (sql.getBinds() != null) {
            sql.getBinds().forEach(bind -> allSql.add(compileSql(bind.getSql())));
        }
        return allSql;
    }

    /**
     * 释放连接,在执行完sql后,将释放此链接
     */
    public abstract void releaseConnection(Connection connection) throws SQLException;

    @Override
    public <T> List<T> list(SQL sql, ObjectWrapper<T> wrapper) throws SQLException {
        if (sql instanceof EmptySQL) return new ArrayList<>();
        //将sql模板编译为可执行的sql
        SQLInfo info = compileSql(sql);
        printSql(info);//打印sql信息
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> datas = new ArrayList<>();
        try {
            //预编译SQL
            statement = connection.prepareStatement(info.getSql());
            this.preparedParam(statement, info);
            //执行sql
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            //获取到执行sql后返回的列信息
            List<String> headers = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                headers.add(metaData.getColumnLabel(i));
            }
            wrapper.setUp(headers);
            int index = 0;
            while (resultSet.next()) {
                //调用包装器,将查询结果包装为对象
                T data = wrapper.newInstance();
                for (int i = 0; i < headers.size(); i++) {
                    Object value = resultSet.getObject(i + 1);
                    wrapper.wrapper(data, index, headers.get(i), value);
                }
                index++;
                if (wrapper.done(data))
                    datas.add(data);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("<==      total: {}", index);
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            //重置JDBC链接
            releaseConnection(connection);
        }
        return datas;
    }

    protected void closeResultSet(ResultSet resultSet) {
        try {
            if (null != resultSet)
                resultSet.close();
        } catch (SQLException e) {
            logger.error("close ResultSet error", e);
        }
    }

    protected void closeStatement(Statement statement) {
        try {
            if (null != statement)
                statement.close();
        } catch (SQLException e) {
            logger.error("close ResultSet error", e);
        }
    }

    @Override
    public <T> T single(SQL sql, ObjectWrapper<T> wrapper) throws SQLException {
        if (sql instanceof EmptySQL) return null;
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        T data = null;
        try {
            statement = connection.prepareStatement(info.getSql());
            //预编译参数
            this.preparedParam(statement, info);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            List<String> headers = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                headers.add(metaData.getColumnLabel(i));
            }
            wrapper.setUp(headers);
            int index = 0;
            if (resultSet.next()) {
                data = wrapper.newInstance();
                for (int i = 0; i < headers.size(); i++) {
                    Object value = resultSet.getObject(i + 1);
                    wrapper.wrapper(data, index, headers.get(i), value);
                }
                index++;
                wrapper.done(data);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("<==      total: {}", index);
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection);
        }
        return data;
    }

    @Override
    public void exec(SQL sql) throws SQLException {
        if (sql instanceof EmptySQL) return;
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(info.getSql());
            //预编译参数
            this.preparedParam(statement, info);
            statement.execute();
            if (sql.getBinds() != null) {
                for (BindSQL bindSQL : sql.getBinds()) {
                    exec(bindSQL.getSql());
                }
            }
        } finally {
            closeStatement(statement);
            releaseConnection(connection);
        }
    }

    @Override
    public int update(SQL sql) throws SQLException {
        if (sql instanceof EmptySQL) return 0;
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        int i = 0;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(info.getSql());
            this.preparedParam(statement, info);
            i = statement.executeUpdate();
            if (sql.getBinds() != null) {
                for (BindSQL bindSQL : sql.getBinds()) {
                    i += update(bindSQL.getSql());
                }
            }
            return i;
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("<==    updated: {} rows", i);
            closeStatement(statement);
            releaseConnection(connection);
        }
    }

    @Override
    public int delete(SQL sql) throws SQLException {
        if (sql instanceof EmptySQL) return 0;
        SQLInfo info = compileSql(sql);
        printSql(info);
        Connection connection = getConnection();
        int i;
        try {
            PreparedStatement statement = connection.prepareStatement(info.getSql());
            this.preparedParam(statement, info);
            i = statement.executeUpdate();
            if (sql.getBinds() != null) {
                for (BindSQL bindSQL : sql.getBinds()) {
                    i += delete(bindSQL.getSql());
                }
                return i;
            }
            if (logger.isDebugEnabled())
                logger.debug("<==     delete: {} rows", i);
            closeStatement(statement);
        } finally {
            releaseConnection(connection);
        }
        return i;
    }

    @Override
    public int insert(SQL sql) throws SQLException {
        return update(sql);
    }

    @Override
    public boolean tableExists(String tname) throws SQLException {
        Connection connection = getConnection();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData
                    .getTables(connection.getCatalog(), null, tname.toUpperCase(), null);
            if (resultSet.next()) return true;
            else {
                resultSet = metaData
                        .getTables(connection.getCatalog(), null, tname.toLowerCase(), null);
            }
            if (resultSet.next()) return true;
        } finally {
            releaseConnection(connection);
        }
        return false;
    }

    /**
     * 预编译参数
     *
     * @param statement
     * @param info
     * @throws Exception
     */
    protected void preparedParam(PreparedStatement statement, SQLInfo info) throws SQLException {
        int index = 1;
        //预编译参数
        for (Object object : info.getParam()) {
            if (object == null)
                statement.setNull(index++, Types.NULL);
            else if (object instanceof Date)
                statement.setTimestamp(index++, new java.sql.Timestamp(((Date) object).getTime()));
            else if (object instanceof byte[]) {
                statement.setBlob(index++, new ByteArrayInputStream((byte[]) object));
            } else
                statement.setObject(index++, object);
        }
    }

    protected void printSql(SQLInfo info) {
        if (logger.isDebugEnabled()) {
            logger.debug("==>  Preparing: {}", info.getSql());
            if (info.getParam() != null && info.getParam().length > 0) {
                logger.debug("==> Parameters: {}", info.paramsString());
                Object[] param = info.getParam();
                if (param.length > 200) return;
                String sim = info.getSql();
                for (int i = 0; i < param.length; i++) {
                    Object obj = param[i];
                    try {
                        sim = sim.replaceFirst("\\?", obj instanceof Number ? String.valueOf(obj) : "'".concat(escapeExprSpecialWord(String.valueOf(obj))).concat("'"));
                    } catch (Exception e) {
                    }
                }
                logger.debug("==>  Simulated: {}", sim);
            }
        }
    }

    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isNullOrEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static class SQLInfo {
        /**
         * sql语句
         */
        private String sql;

        /**
         * 参数列表
         */
        private Object[] param;

        /**
         * 参数字符串
         */
        private String paramString;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Object[] getParam() {
            return param;
        }

        public void setParam(Object[] param) {
            this.param = param;
        }

        public String paramsString() {
            if (getParam() == null)
                return "";
            if (paramString == null) {
                StringBuilder builder = new StringBuilder();
                int i = 0;
                for (Object param : getParam()) {
                    if (i++ != 0)
                        builder.append(",");
                    builder.append(String.valueOf(param));
                    builder.append("(");
                    builder.append(param == null ? "null" : param.getClass().getSimpleName());
                    builder.append(")");
                }
                paramString = builder.toString();
            }
            return paramString;
        }

    }
}


