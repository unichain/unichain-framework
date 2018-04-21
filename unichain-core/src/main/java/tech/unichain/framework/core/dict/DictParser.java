package tech.unichain.framework.core.dict;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictParser {
    String getId();

    String parseText(DictDefine dictDefine, String value);

    String parseValue(DictDefine dictDefine, String text);
}
