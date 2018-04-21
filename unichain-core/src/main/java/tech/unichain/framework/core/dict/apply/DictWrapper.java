package tech.unichain.framework.core.dict.apply;

import tech.unichain.framework.core.dict.DictDefineRepository;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
public interface DictWrapper {
    DictWrapper empty = (bean, repository) -> {};

    void wrap(Object bean, DictDefineRepository repository);


}
