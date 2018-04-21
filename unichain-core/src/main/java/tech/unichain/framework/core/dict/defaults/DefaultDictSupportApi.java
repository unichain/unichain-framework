package tech.unichain.framework.core.dict.defaults;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import tech.unichain.framework.core.dict.ClassDictDefine;
import tech.unichain.framework.core.dict.DictDefineRepository;
import tech.unichain.framework.core.dict.DictParser;
import tech.unichain.framework.core.dict.DictSupportApi;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
@Slf4j
public class DefaultDictSupportApi implements DictSupportApi {

    private DictDefineRepository repository;

    private Map<String, DictParser> parserRepo = new HashMap<>();

    public DefaultDictSupportApi(DictDefineRepository repository) {
        this.repository = repository;
        DictParser defaultParser = new DefaultDictParser();
        parserRepo.put(defaultParser.getId(), defaultParser);
    }

    @Override
    public DictParser getParser(String id, String defaultId) {
        return Optional.ofNullable(parserRepo.get(id)).orElseGet(() -> parserRepo.get(defaultId));
    }

    @Override
    public <T> T unwrap(T target) {
        if (target == null) {
            return null;
        }
        if (target instanceof Map) {
            return target;
        }
        if (target instanceof List) {
            return (T) ((List) target).stream()
                    .map(this::wrap)
                    .collect(Collectors.toList());
        }
        Class type = ClassUtils.getUserClass(target);
        List<ClassDictDefine> defines = repository.getDefine(type);
        if (defines.isEmpty()) {
            return target;
        }
        for (ClassDictDefine define : defines) {
            String fieldName = define.getField();
            String alias = define.getAlias();
            if (StringUtils.isEmpty(alias)) {
                continue;
            }
            try {
                Object fieldValue = BeanUtils.getProperty(target, fieldName);
                if (fieldValue != null) {
                    continue;
                }
                Object value = BeanUtils.getProperty(target, alias);
                if (value == null) {
                    continue;
                }
                BeanUtils.setProperty(target, fieldName, getParser(define.getParserId()).parseValue(define, String.valueOf(value)));
            } catch (Exception e) {
                log.warn("unwrap error", e.getMessage());
            }
        }
        return target;
    }

    @Override
    @SuppressWarnings("all")
    public <T> T wrap(T target) {
        if (target == null) {
            return null;
        }
        if (target instanceof Map) {
            return target;
        }
        if (target instanceof List) {
            return (T) ((List) target).stream().map(this::wrap).collect(Collectors.toList());
        }
        Class type = ClassUtils.getUserClass(target);
        List<ClassDictDefine> defines = repository.getDefine(type);
        if (defines.isEmpty()) {
            return target;
        }
        for (ClassDictDefine define : defines) {
            String fieldName = define.getField();
            String alias = define.getAlias();
            if (StringUtils.isEmpty(alias)) {
                continue;
            }
            try {
                Object value = BeanUtils.getProperty(target, fieldName);
                if (value == null) {
                    continue;
                }
                BeanUtils.setProperty(target, alias, getParser(define.getParserId()).parseText(define, String.valueOf(value)));
            } catch (Exception e) {
                log.warn("wrap error", e.getMessage());
            }
        }
        return target;
    }
}
