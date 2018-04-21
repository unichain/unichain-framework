package tech.unichain.framework.core.dict;

import java.util.List;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictDefineRepository {
    DictDefine getDefine(String id);

    List<ClassDictDefine> getDefine(Class type);

    void addDefine(DictDefine dictDefine);
}
