package tech.unichain.framework.system.datasource.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  数据源配置
 *
 * @author lait.zhang@gmail.com
 */
@RestController
@RequestMapping("datasource/config")
@Authorize(permission = "data-source-config",description = "动态数据源管理")
@Api(value = "数据源配置",tags = "动态数据源-数据源配置")
public class DataSourceConfigController implements SimpleGenericEntityController<DataSourceConfigEntity, String, QueryParamEntity> {

    private DataSourceConfigService dataSourceConfigService;
  
    @Autowired
    public void setDataSourceConfigService(DataSourceConfigService dataSourceConfigService) {
        this.dataSourceConfigService = dataSourceConfigService;
    }
  
    @Override
    public DataSourceConfigService getService() {
        return dataSourceConfigService;
    }
}
