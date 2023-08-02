package org.ycframework.annotation;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@YcComponent
public @interface YcService {
    String value() default "";
}
