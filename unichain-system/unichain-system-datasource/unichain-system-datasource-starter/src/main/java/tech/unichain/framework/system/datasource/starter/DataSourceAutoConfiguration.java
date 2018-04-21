package tech.unichain.framework.system.datasource.starter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lait.zhang
 */
@Configuration
@ComponentScan({"org.hswebframework.web.service.datasource.simple"
        , "org.hswebframework.web.controller.datasource"})
public class DataSourceAutoConfiguration {
}
