package tech.unichain.framework.core.dict.defaults;

import tech.unichain.framework.core.dict.DictDefine;
import tech.unichain.framework.core.dict.DictParser;
import tech.unichain.framework.core.dict.ItemDefine;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public class DefaultDictParser implements DictParser {
    @Override
    public String getId() {
        return "default";
    }

    @Override
    public String parseText(DictDefine dictDefine, String value) {
        return dictDefine.getItems()
                .stream()
                .filter(itemDefine -> itemDefine.getValue().equals(value))
                .map(ItemDefine::getText)
                .findFirst()
                .orElse(value);
    }

    @Override
    public String parseValue(DictDefine dictDefine, String text) {
        return dictDefine.getItems()
                .stream()
                .filter(itemDefine -> itemDefine.getText().equals(text))
                .map(ItemDefine::getValue)
                .findFirst()
                .orElse(text);
    }
}
