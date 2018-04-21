package tech.unichain.framework.core.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.unichain.framework.core.dict.EnumDict;

@Getter
@AllArgsConstructor
public enum Color implements EnumDict<Integer> {
    RED(1, "红色"),
    BLUE(2, "蓝色");

    private Integer value;

    private String text;

}
