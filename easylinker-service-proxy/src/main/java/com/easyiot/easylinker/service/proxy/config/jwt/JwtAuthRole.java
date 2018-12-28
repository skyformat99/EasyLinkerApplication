package com.easyiot.easylinker.service.proxy.config.jwt;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface JwtAuthRole {
    String[] roles() default {"ROLE_USER"};
}
