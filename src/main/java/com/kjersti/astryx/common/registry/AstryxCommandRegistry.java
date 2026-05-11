package com.kjersti.astryx.common.registry;

import com.kjersti.astryx.common.Maven;
import com.kjersti.astryx.common.annotations.ProgramCommand;
import com.kjersti.astryx.common.annotations.ProgramCommandWrapper;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class AstryxCommandRegistry {
    public static final Logger LOGGER = AstryxLogManager.getLogger("cmd");

    public static final List<ProgramCommandWrapper> commands = AnnotationRegistry.astryxCommands;
    public static final List<ProgramCommandWrapper> visibleCommands = commands.stream()
            .filter(ProgramCommandWrapper::isVisible)
            .toList();

    public static void init(Maven mavenData) {
        List<String> duplicateIds = getDuplicateIds();

        duplicateIds = duplicateIds.stream()
                .distinct()
                .toList();

        for (String duplicate: duplicateIds) {
            LOGGER.warn("Duplicate command id {} found. This command may have unintended effects.", duplicate);
        }

        LOGGER.info("Registered {} {} commands", commands.size(), mavenData.getBotName());
    }

    public static void onCommandLineInput(String command) {
        String commandPrefix = SettingRegistry.getTerminalPrefix();
        String[] commandInfo = getCommandInfo(command);

        if (!isValidCommand(commandInfo[0])) return;

        command = command.replace(commandPrefix, "");
        ProgramCommandWrapper commandObj = getCommandObjFromStr(commandInfo[0]);

        if (commandObj == null) {
            onInvalidCommand(command);
        } else {
            String[] args = getArgumentsFromLine(commandInfo);

            commandObj.run(args);
        }
    }

    public static String[] getCommandInfo(String command) {
        String[] splitInfo = command.split(" ");

        return Arrays.stream(splitInfo)
                .map(String::trim)
                .toArray(String[]::new);
    }

    public static ProgramCommandWrapper getCommandObjFromStr(String commandId) {
        Optional<ProgramCommandWrapper> command = commands.stream()
                .filter(obj -> obj.getId().equals(commandId))
                .findFirst();

        return command.orElse(null);
    }

    public static List<String> getDuplicateIds() {
        List<String> commandIds = commands.stream()
                .map(ProgramCommandWrapper::getId)
                .toList();

        Map<String, Long> freqMap = commandIds.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        return commandIds.stream()
                .filter(e -> freqMap.get(e) > 1)
                .toList();
    }

    public static boolean isValidCommand(String command) {
        String commandPrefix = SettingRegistry.getTerminalPrefix();

        return command.startsWith(commandPrefix) && !command.isEmpty();
    }

    public static void onInvalidCommand(String commandId) {
        LOGGER.warn("Invalid command id {}", commandId);
    }

    public static String[] getArgumentsFromLine(String[] splitLine) {
        String[] args = new String[splitLine.length-1];

        System.arraycopy(splitLine, 1, args, 0, splitLine.length - 1);

        return args;
    }
}
