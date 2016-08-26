package com.lizy.retrofit.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by lizy on 16-8-26.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Get {
    String value() default "";
}
