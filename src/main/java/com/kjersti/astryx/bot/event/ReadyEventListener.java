//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.event;

import com.kjersti.astryx.bot.Bot;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class ReadyEventListener implements BaseEvent {
    public ReadyEventListener() {
    }

    public static void runEvent(User bot, ReadyEvent event) {
        Bot.LOGGER.info("Logged in as " + bot.getUsername() + "#" + bot.getDiscriminator());
    }

    public Mono<Void> registerEvent(GatewayDiscordClient gateway) {
        return gateway.on(ReadyEvent.class, (event) -> Mono.fromRunnable(() -> {
            runEvent(event.getSelf(), event);
        })).then();
    }
}
