//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.util;

import com.kjersti.astryx.bot.Bot;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.registry.TokenRegistry;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BotUtil {
    public BotUtil() {
    }

    public static BigInteger getCategoryIdFromChannel(Mono<MessageChannel> channel) {
        return ((GuildMessageChannel)channel.block()).getCategoryId().get().asBigInteger();
    }

    public static List<BigInteger> getGuildIdsFromChannels(GatewayDiscordClient client, List<BigInteger> channelIds) {
        return channelIds.stream()
                .map(Snowflake::of)
                .map(client::getChannelById)
                .map(Mono::block)
                .map(channel -> (GuildChannel) channel)
                .map(GuildChannel::getGuild)
                .map(Mono::block).filter(Objects::nonNull)
                .map(Guild::getId)
                .map(Snowflake::asBigInteger)
                .distinct()
                .toList();
    }

    public static List<BigInteger> filterGuildIds(GatewayDiscordClient client, List<BigInteger> guildIds) {
        Flux<Guild> guildsFlux = client.getGuilds();

        Set<BigInteger> botGuildIds = guildsFlux
                .map(Guild::getId)
                .map(id -> new BigInteger(id.asString()))
                .collect(Collectors.toSet())
                .block();

        return guildIds.stream()
                .filter(botGuildIds::contains)
                .toList();
    }

    public static String getToken() {
        if (SettingRegistry.useTestDummyDiscord()) {
            Bot.LOGGER.warn("Discord test dummy enabled, using test dummy");

            return TokenRegistry.getTestDummyDiscordToken();
        }
        Bot.LOGGER.warn("Discord test dummy disabled, using live bot");

        return TokenRegistry.getDiscordToken();
    }

    public static List<BigInteger> filterChannelIds(GatewayDiscordClient client, List<BigInteger> channelIds) {
        return client.getGuilds()
                .flatMap(Guild::getChannels)
                .map(Channel::getId)
                .map(Snowflake::asBigInteger)
                .collectList()
                .map(availableChannelIds ->
                        channelIds.stream()
                                .filter(availableChannelIds::contains)
                                .collect(Collectors.toList())
                )
                .block();
    }
}
