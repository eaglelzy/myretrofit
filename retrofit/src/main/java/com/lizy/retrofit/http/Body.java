package com.lizy.retrofit.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by lizy on 16-8-29.
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Body {
}
