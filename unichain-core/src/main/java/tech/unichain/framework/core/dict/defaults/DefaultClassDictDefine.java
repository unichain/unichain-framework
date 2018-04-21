package tech.unichain.framework.core.dict.defaults;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.unichain.framework.core.dict.ClassDictDefine;
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
public class DefaultClassDictDefine implements ClassDictDefine {
    private String           field;
    private String           id;
    private String           alias;
    private String           comments;
    private String           parserId;
    private List<ItemDefine> items;
}
