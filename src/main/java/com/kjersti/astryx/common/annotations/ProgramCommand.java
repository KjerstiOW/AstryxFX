package com.kjersti.astryx.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProgramCommand {
    String id();
    String desc();
    boolean visible() default true;
    boolean enabled() default true;
}