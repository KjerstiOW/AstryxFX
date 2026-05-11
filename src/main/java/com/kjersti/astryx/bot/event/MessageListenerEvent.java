//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.event;

import com.kjersti.astryx.bot.Bot;
import com.kjersti.astryx.bot.embed.ErrorEmbed;
import com.kjersti.astryx.bot.embed.MapSummaryEmbed;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.registry.TeamRegistry;
import com.kjersti.astryx.common.registry.object.TeamRegistryObject;
import com.kjersti.astryx.parsing.MessageParseManager;
import com.kjersti.astryx.parsing.object.OverwatchMap;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class MessageListenerEvent implements BaseEvent {
    public MessageListenerEvent() {}

    public static void runEvent(MessageCreateEvent event) {
        if (isMessageValid(event) && isValidMapChannel(event)) {
            TeamRegistryObject teamRegistryObject = getTeamObjectFromEvent(event);

            if (teamRegistryObject != null) {
                TextChannel channel = getChannelFromEvent(event);

                if (channel != null) {
                    try {
                        processEvent(event, channel);
                    } catch (IllegalArgumentException var4) {
                        handleException(channel, var4);
                    }

                }
            }
        }
    }

    private static void processEvent(MessageCreateEvent event, TextChannel channel) {
        MessageParseManager manager = new MessageParseManager(event.getMessage().getContent());

        List<OverwatchMap> maps = manager.parse();
        if (maps != null) {
            String sqlLocation = buildSqlLocation();
            manager.addMapsToDatabase(maps, sqlLocation);
            EmbedCreateSpec mapEmbed = (new MapSummaryEmbed(maps, sqlLocation)).getEmbed();
            sendEmbedAndCleanup(channel, mapEmbed, event);
        }
    }

    private static TextChannel getChannelFromEvent(MessageCreateEvent event) {
        return (TextChannel)event.getMessage().getChannel().block();
    }

    private static String buildSqlLocation() {
        return SettingRegistry.getMapDatabase() + "." + SettingRegistry.getMapDataTable();
    }

    private static void sendEmbedAndCleanup(TextChannel channel, EmbedCreateSpec mapEmbed, MessageCreateEvent event) {
        channel.createMessage(new EmbedCreateSpec[]{mapEmbed}).subscribe();
        event.getMessage().delete().subscribe();
    }

    public static boolean isMessageValid(MessageCreateEvent event) {
        return event.getMessage().getContent().contains("vs");
    }

    public static boolean isValidMapChannel(MessageCreateEvent event) {
        BigInteger channelId = event.getMessage().getChannelId().asBigInteger();
        return TeamRegistry.getRegistry().stream()
                .anyMatch((team) -> team.matchesChannelId(channelId));
    }

    public static TeamRegistryObject getTeamObjectFromEvent(MessageCreateEvent event) {
        BigInteger channelId = event.getMessage().getChannelId().asBigInteger();

        Optional<TeamRegistryObject> discordTeam = TeamRegistry.getRegistry().stream()
                .filter((obj) -> obj.getCodesId()
                        .equals(channelId))
                .findFirst();
        return discordTeam.isEmpty() ? null : discordTeam.get();
    }

    public static void handleException(TextChannel channel, Exception ex) {
        try {
            EmbedCreateSpec errorEmbed = (new ErrorEmbed(ex)).getEmbed();

            channel.createMessage(errorEmbed).subscribe();

            Bot.LOGGER.warn("Astryx has handled an exception: " + ex.getClass().getSimpleName() + " (" + ((channel.getGuild().block()).getName() + " #" + channel.getName() + ")"));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public Mono<Void> registerEvent(GatewayDiscordClient gateway) {
        return gateway.on(MessageCreateEvent.class, (event) -> Mono.fromRunnable(() -> {
            runEvent(event);
        })).then();
    }
}
