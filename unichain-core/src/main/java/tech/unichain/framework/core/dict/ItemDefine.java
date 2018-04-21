package tech.unichain.framework.core.dict;

import java.util.List;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface ItemDefine {
    String getText();

    String getValue();

    String getComments();

    List<ItemDefine> getChildren();

}
