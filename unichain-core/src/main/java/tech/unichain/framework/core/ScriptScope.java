package tech.unichain.framework.core;

import java.lang.annotation.*;

/**
 * @author lait.zhang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScriptScope {

    String value() default "";
}
