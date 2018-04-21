package tech.unichain.framework.orm.rdb.meta.parser;

import tech.unichain.framework.orm.rdb.meta.RDBTableMetaData;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lait.zhang@gmail.com on 16-6-5.
 */
public interface TableMetaParser {
    RDBTableMetaData parse(String name);

    boolean tableExists(String name);

    List<RDBTableMetaData> parseAll() throws SQLException;
}
