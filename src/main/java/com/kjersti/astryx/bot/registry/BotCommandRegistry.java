//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.registry;

import com.kjersti.astryx.bot.Bot;
import com.kjersti.astryx.bot.util.BotUtil;
import com.kjersti.astryx.common.annotations.BotCommandWrapper;
import com.kjersti.astryx.common.registry.AnnotationRegistry;
import com.kjersti.astryx.common.registry.TeamRegistry;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;

import java.math.BigInteger;
import java.util.*;

public class BotCommandRegistry {
    private static final List<BotCommandWrapper> registry = AnnotationRegistry.botCommands;

    public static void runCommand(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        String commandName = event.getCommandName();
        BotCommandWrapper command = getObjectFromName(commandName);

        if (command != null) {
            command.run(client, event);
        }
    }

    public static void updateCommands(GatewayDiscordClient gateway) {
        registerCommands(gateway);
    }

    public static void registerCommands(GatewayDiscordClient gateway) {
        List<BigInteger> allChannels = TeamRegistry.getAllCodeChannelIds();
        List<BigInteger> filteredChannels = BotUtil.filterChannelIds(gateway, allChannels);
        List<BigInteger> guildIds = BotUtil.getGuildIdsFromChannels(gateway, filteredChannels);
        List<BigInteger> filteredIds = BotUtil.filterGuildIds(gateway, guildIds);

        long applicationId = gateway.getSelfId().asLong();

        for (BigInteger guildId: filteredIds) {
            for (BotCommandWrapper command: registry) {
                gateway.getRestClient()
                        .getApplicationService()
                        .createGuildApplicationCommand(applicationId, guildId.longValue(), command.buildRequest())
                        .subscribe();
            }
        }

        Bot.LOGGER.info("Registered {} command(s) in {} server(s)", registry.size(), guildIds.size());
    }

    private static BotCommandWrapper getObjectFromName(String commandName) {
        Optional<BotCommandWrapper> command = registry.stream()
                .filter(name -> name.getId().equals(commandName))
                .findFirst();

        if (command.isEmpty()) return null;

        return command.get();
    }

    public static List<BotCommandWrapper> getRegistry() {
        return registry.stream().toList();
    }
}
