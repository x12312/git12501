package org.ycframework.annotation;

import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.support.PropertySourceFactory;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YcPropertySource {
    String[] value();
}

