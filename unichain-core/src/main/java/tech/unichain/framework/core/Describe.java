package tech.unichain.framework.core;

import java.lang.annotation.*;

/**
 * 功能描述
 *
 * @author lait.zhang
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Describe {
    String value();

    Class type() default Object.class;
}
