package com.kjersti.astryx.common.annotations;

import discord4j.discordjson.json.ApplicationCommandOptionData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BotCommand {
    String id();
    String desc();
    boolean enabled() default true;
    String[] arguments() default {};
}