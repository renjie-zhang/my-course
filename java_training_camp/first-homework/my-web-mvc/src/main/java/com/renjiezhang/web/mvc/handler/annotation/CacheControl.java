package com.renjiezhang.web.mvc.handler.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheControl {

    String[] value() default {};
}
