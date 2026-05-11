//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.event;

import com.kjersti.astryx.bot.registry.BotCommandRegistry;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import reactor.core.publisher.Mono;

public class CommandEventListener implements BaseEvent {
    public CommandEventListener() {}

    public Mono<Void> registerEvent(GatewayDiscordClient gateway) {
        return gateway.on(ApplicationCommandInteractionEvent.class, (event) -> Mono.fromRunnable(() -> {
            this.runEvent(gateway, event);
        })).then();
    }

    public void runEvent(GatewayDiscordClient gateway, ApplicationCommandInteractionEvent event) {
        BotCommandRegistry.runCommand(gateway, event);
    }
}
