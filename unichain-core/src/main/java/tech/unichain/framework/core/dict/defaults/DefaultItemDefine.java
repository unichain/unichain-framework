package tech.unichain.framework.core.dict.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.unichain.framework.core.dict.ItemDefine;

import java.util.List;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultItemDefine implements ItemDefine {
    private String           text;
    private String           value;
    private String           comments;
    private List<ItemDefine> children;
}
