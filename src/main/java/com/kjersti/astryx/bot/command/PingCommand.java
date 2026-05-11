//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.command;

import com.kjersti.astryx.bot.embed.LackOfPermsEmbed;
import com.kjersti.astryx.bot.embed.WaitingResponseEmbed;
import com.kjersti.astryx.bot.util.BotUtil;
import com.kjersti.astryx.common.annotations.BotCommand;
import com.kjersti.astryx.common.python.PythonRunner;
import com.kjersti.astryx.common.registry.TeamRegistry;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.math.BigInteger;

@BotCommand(
        id="ping",
        desc="pong!"
)
public class PingCommand implements BaseCommand {
    public PingCommand() {}

    @Override
    public Mono<Void> run(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        return event.reply("Pong!");
    }

    @Override
    public boolean isValidCommand(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        return true;
    }
}
