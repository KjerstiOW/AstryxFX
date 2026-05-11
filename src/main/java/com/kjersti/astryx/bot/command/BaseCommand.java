//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.command;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public interface BaseCommand {
    Mono<Void> run(GatewayDiscordClient client, ApplicationCommandInteractionEvent event);

    boolean isValidCommand(GatewayDiscordClient client, ApplicationCommandInteractionEvent event);
}
