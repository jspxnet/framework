package com.github.jspxnet.txweb.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
