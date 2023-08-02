package org.ycframework.annotation;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YcBean {
    String[] value() default {};
}
