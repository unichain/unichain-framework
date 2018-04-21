package tech.unichain.framework.orm.core;

import tech.unichain.framework.orm.core.meta.TableMetaData;

public interface ValidatorFactory {
    Validator createValidator(TableMetaData tableMetaData);
}
