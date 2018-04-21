package tech.unichain.framework.orm.core.meta;

import tech.unichain.framework.orm.core.ObjectWrapperFactory;
import tech.unichain.framework.orm.core.ValidatorFactory;
import tech.unichain.framework.orm.core.meta.storage.MapTableMetaDataStorage;
import tech.unichain.framework.orm.core.meta.storage.TableMetaDataStorage;

public abstract class AbstractDatabaseMetaData implements DatabaseMetaData {
    protected ObjectWrapperFactory objectWrapperFactory;
    protected ValidatorFactory validatorFactory;
    protected TableMetaDataStorage tableMetaDataStorage =new MapTableMetaDataStorage();

    @Override
    public <T extends TableMetaData> T getTableMetaData(String name) {
        return tableMetaDataStorage.getTableMetaData(name);
    }

    @Override
    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    @Override
    public ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public void setTableMetaDataStorage(TableMetaDataStorage tableMetaDataStorage) {
        this.tableMetaDataStorage = tableMetaDataStorage;
    }
}
