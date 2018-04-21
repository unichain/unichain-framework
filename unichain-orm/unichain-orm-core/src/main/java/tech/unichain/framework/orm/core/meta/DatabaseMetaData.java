package tech.unichain.framework.orm.core.meta;

import tech.unichain.framework.orm.core.ObjectWrapperFactory;
import tech.unichain.framework.orm.core.ValidatorFactory;

public interface DatabaseMetaData {

    ObjectWrapperFactory getObjectWrapperFactory();

    ValidatorFactory getValidatorFactory();

    <T extends TableMetaData> T getTableMetaData(String name);

}
