package tech.unichain.framework.system.datasource.entity;

import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

/**
 * 数据源配置
 *
 * @author lait.zhang@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDataSourceConfigEntity extends SimpleGenericEntity<String> implements DataSourceConfigEntity {
    //数据源名称
    private String         name;
    //是否启用
    private Long           enabled;
    //创建日期
    private java.util.Date createDate;
    //备注
    private String         describe;
}