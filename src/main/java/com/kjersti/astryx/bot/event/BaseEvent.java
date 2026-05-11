//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.event;

import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;

public interface BaseEvent {
    Mono<Void> registerEvent(GatewayDiscordClient var1);
}
