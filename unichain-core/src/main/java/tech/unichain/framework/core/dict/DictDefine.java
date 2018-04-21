package tech.unichain.framework.core.dict;

import java.io.Serializable;
import java.util.List;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictDefine extends Serializable {
    String getId();

    String getAlias();

    String getComments();

    String getParserId();

    List<ItemDefine> getItems();

}
