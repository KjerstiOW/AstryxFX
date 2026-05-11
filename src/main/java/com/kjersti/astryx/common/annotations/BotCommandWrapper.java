package com.kjersti.astryx.common.annotations;

import com.kjersti.astryx.Astryx;
import com.kjersti.astryx.bot.Bot;
import com.kjersti.astryx.bot.command.BaseCommand;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.lang.reflect.Method;

public class BotCommandWrapper {
    private final Class<BotCommand> clazz;
    private final Method buildMethod;
    private final String id;
    private final String desc;
    private final boolean enabled;

    public BotCommandWrapper(Class<BotCommand> clazz, BotCommand annotation) {
        this.clazz = clazz;
        this.id = annotation.id();
        this.desc = annotation.desc();
        this.enabled = annotation.enabled();
        this.buildMethod = null;
    }

    public BotCommandWrapper(Class<BotCommand> clazz, BotCommand annotation, Method buildMethod) {
        this.clazz = clazz;
        this.id = annotation.id();
        this.desc = annotation.desc();
        this.enabled = annotation.enabled();
        this.buildMethod = buildMethod;
    }

    public void run(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        try {
            BaseCommand instance = (BaseCommand) clazz.getDeclaredConstructor().newInstance();

            if (instance.isValidCommand(client, event)) {
                String guildName = ((Guild)event.getInteraction().getGuild().block()).getName();
                String channelName = ((TextChannel)event.getInteraction().getChannel().block()).getName();
                String location = guildName + " #" + channelName;
                String user = event.getInteraction().getUser().getTag();

                Bot.LOGGER.info("Executing command '" + id + "', location={" + location + "}, user={" + user + "}");
                instance.run(client, event).subscribe();
                Bot.LOGGER.info("Command executed: '" + id + "', location={" + location + "}, user={" + user + "}");
            }
        } catch (Exception e) {
            Astryx.LOGGER.error(e);
        }
    }

    public ApplicationCommandRequest defaultBuildRequest() {
        return ApplicationCommandRequest.builder()
                .name(id)
                .description(desc)
                .build();
    }

    public ApplicationCommandRequest buildRequest() {
        if (this.buildMethod == null) return defaultBuildRequest();

        try {
            BaseCommand instance = (BaseCommand) clazz.getDeclaredConstructor().newInstance();

            return (ApplicationCommandRequest) this.buildMethod.invoke(instance);
        } catch (Exception e) {
            Astryx.LOGGER.error("Error building bot command '" + clazz.getSimpleName() + "'. Using default builder");
            return defaultBuildRequest();
        }
    }

    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "BotCommandWrapper{" +
                "clazz=" + clazz +
                ", id='" + id + '\'' +
                ", desc='" + desc + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
