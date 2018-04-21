package tech.unichain.framework.core.dict;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictSupportApi {
    DictParser getParser(String id, String defaultId);

    default DictParser getParser(String id) {
        return getParser(id, "default");
    }

    <T> T wrap(T target);

    <T> T unwrap(T target);

}
