//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot;

import com.kjersti.astryx.bot.registry.BotCommandRegistry;
import com.kjersti.astryx.bot.event.BaseEvent;
import com.kjersti.astryx.bot.event.CommandEventListener;
import com.kjersti.astryx.bot.event.MessageListenerEvent;
import com.kjersti.astryx.bot.event.ReadyEventListener;
import com.kjersti.astryx.bot.util.BotUtil;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.TokenRegistry;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class Bot {
    public static final Logger LOGGER = AstryxLogManager.getLogger("bot");

    public void run() {
        LOGGER.info("Attempting astryx.bot startup");
        String token = BotUtil.getToken();

        DiscordClient client = DiscordClient.create(token);
        Mono<Void> login = client.withGateway((gateway) -> {
            BotCommandRegistry.registerCommands(gateway);
            return this.registerEvents(gateway);
        });
        login.block();
    }

    private Mono<Void> registerEvents(GatewayDiscordClient gateway) {
        List<BaseEvent> events = List.of(
                new ReadyEventListener(),
                new MessageListenerEvent(),
                new CommandEventListener()
        );

        List<Mono<Void>> eventMonos = events.stream()
                .map((event) -> event.registerEvent(gateway))
                .toList();

        LOGGER.info("Registered {} event(s)", eventMonos.size());
        return Flux.merge(eventMonos).then();
    }
}
