package com.kjersti.astryx.common.registry;

import java.lang.reflect.Method;
import java.util.*;

import com.kjersti.astryx.bot.command.BaseCommand;
import com.kjersti.astryx.common.annotations.*;
import com.kjersti.astryx.common.commands.AstryxCommand;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class AnnotationRegistry {
    public static final List<ProgramCommandWrapper> astryxCommands = registerCommands();
    public static final  List<BotCommandWrapper> botCommands = registerBotCommands();

    public static Set<Class<?>> findAllCommands() {
        Set<Class<?>> commands = new HashSet<>();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("com.kjersti.astryx.common.commands"))
                        .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
        );

        Set<Class<?>> annotatedClasses = reflections.getSubTypesOf(Object.class);

        for (Class<?> clazz : annotatedClasses) {
            if (clazz.isAnnotationPresent(ProgramCommand.class)) {
                commands.add(clazz);
            }
        }

        return commands;
    }

    public static Set<Class<?>> findAllBotCommands() {
        Set<Class<?>> commands = new HashSet<>();

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("com.kjersti.astryx.bot.command"))
                        .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
        );

        Set<Class<?>> annotatedClasses = reflections.getSubTypesOf(Object.class);

        for (Class<?> clazz : annotatedClasses) {
            if (clazz.isAnnotationPresent(BotCommand.class)) {
                commands.add(clazz);
            }
        }

        return commands;
    }

    public static List<ProgramCommandWrapper> registerCommands() {
        List<ProgramCommandWrapper> commands = new ArrayList<>();
        Set<Class<?>> unverifiedCommands = findAllCommands();

        for (Class<?> clazz: unverifiedCommands) {
            ProgramCommand classAnnotation = clazz.getAnnotation(ProgramCommand.class);

            if (!AstryxCommand.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Program command does not implement AstryxCommand interface. Class: " + clazz.getName());
            }

            Class<AstryxCommand> explicitCommand = (Class<AstryxCommand>) clazz;

            ProgramCommandWrapper command = new ProgramCommandWrapper(explicitCommand, classAnnotation);

            commands.add(command);
        }

        return commands.stream()
                .filter(ProgramCommandWrapper::isEnabled)
                .toList();
    }

    public static List<BotCommandWrapper> registerBotCommands() {
        List<BotCommandWrapper> commands = new ArrayList<>();
        Set<Class<?>> unverifiedCommands = findAllBotCommands();

        for (Class<?> clazz: unverifiedCommands) {
            BotCommand classAnnotation = clazz.getAnnotation(BotCommand.class);
            Method[] methods = clazz.getMethods();

            if (!BaseCommand.class.isAssignableFrom(clazz)) {
                throw new RuntimeException("Bot command does not implement BaseCommand interface. Class: " + clazz.getName());
            }

            Class<BotCommand> explicitCommand = (Class<BotCommand>) clazz;

            Method buildMethod = null;

            for (Method method: methods) {
                if (method.isAnnotationPresent(BotBuilder.class)) {
                    buildMethod = method;

                    break;
                }
            }
            BotCommandWrapper command;

            if (buildMethod == null) {
                command = new BotCommandWrapper(explicitCommand, classAnnotation);
            } else {
                command = new BotCommandWrapper(explicitCommand, classAnnotation, buildMethod);
            }

            commands.add(command);
        }

        return commands.stream()
                .filter(BotCommandWrapper::isEnabled)
                .toList();
    }
}
