//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kjersti.astryx.bot.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kjersti.astryx.api.ApiHandler;
import com.kjersti.astryx.bot.embed.*;
import com.kjersti.astryx.common.annotations.BotBuilder;
import com.kjersti.astryx.common.annotations.BotCommand;
import com.kjersti.astryx.common.python.PythonRunner;
import com.kjersti.astryx.common.registry.TokenRegistry;
import com.kjersti.astryx.common.util.JsonManager;
import com.kjersti.astryx.common.util.StringUtil;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@BotCommand(
        id="team_wins",
        desc="Gets team wins"
)
public class TeamWinsCommand implements BaseCommand {
    public static final String TEAM_ID_ENDPOINT = "https://open.faceit.com/data/v4/teams/";

    public Mono<Void> run(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        String teamId = getTeamIdFromEvent(event).trim();
        String teamName = getTeamNameFromId(teamId);

        if (teamName == null) {
            this.onInvalidTeam(event, teamId);
            return Mono.empty();
        }

        String startDate = getImpliedStartString(event);
        String endDate = getImpliedEndString(event);

        if (startDate == null) {
            this.onDateError(event, getStartDateFromEvent(event), "Start");
            return Mono.empty();
        }

        if (endDate == null) {
            this.onDateError(event, getEndDateFromEvent(event), "End");
            return Mono.empty();
        }

        this.awaitResponse(event, teamId, teamName);

        InputStream chart = PythonRunner.createMapWinChart(teamId, teamName, startDate, endDate);

        if (chart == null) {
            this.onPythonError(event, teamId, teamName);
        }

        this.editResponse(event, chart);

        try {
            Thread.sleep(1000);
            chart.close();
        } catch (IOException | InterruptedException ignored) {}

        PythonRunner.deleteChart(PythonRunner.MAP_WINS_CHART_PATH);

        return Mono.empty();
    }

    public Mono<Void> lackOfPerms(ApplicationCommandInteractionEvent event) {
        EmbedCreateSpec embed = (new LackOfPermsEmbed()).getEmbed();
        InteractionApplicationCommandCallbackSpec msg = InteractionApplicationCommandCallbackSpec.create().withEmbeds(embed);
        return event.reply(msg);
    }

    public void editResponse(ApplicationCommandInteractionEvent event, InputStream chart) {
        InteractionReplyEditSpec msg = InteractionReplyEditSpec.builder()
                .addFile("chart.png", chart)
                .embeds(new ArrayList<>())
                .build();
        event.editReply(msg).subscribe();
    }

    public void awaitResponse(ApplicationCommandInteractionEvent event, String teamId, String teamName) {
        EmbedCreateSpec embed = (new VetoWaitingResponseEmbed(teamId, teamName)).getEmbed();
        InteractionApplicationCommandCallbackSpec msg = InteractionApplicationCommandCallbackSpec.create().withEmbeds(embed);
        event.reply(msg).subscribe();
    }

    public void onInvalidTeam(ApplicationCommandInteractionEvent event, String teamId) {
        EmbedCreateSpec embed = (new InvalidNameEmbed(teamId)).getEmbed();

        InteractionApplicationCommandCallbackSpec msg = InteractionApplicationCommandCallbackSpec.create().withEmbeds(embed);
        event.reply(msg).subscribe();
    }

    @Override
    public boolean isValidCommand(GatewayDiscordClient client, ApplicationCommandInteractionEvent event) {
        return true;
    }

    @BotBuilder
    public ApplicationCommandRequest buildRequest() {
        return ApplicationCommandRequest.builder()
                .name("team_wins")
                .description("Gets team wins")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("team_id")
                        .description("Faceit Team id")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build()
                )
                .addOption(ApplicationCommandOptionData.builder()
                        .name("start_date")
                        .description("Start Date (YYYY-MM-DD)")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build()
                )
                .addOption(ApplicationCommandOptionData.builder()
                        .name("end_date")
                        .description("End Date (YYYY-MM-DD)")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build()
                )
                .build();
    }

    public String getTeamIdFromEvent(ApplicationCommandInteractionEvent event) {
        return event.getInteraction().getCommandInteraction().get().
                getOption("team_id").get()
                .getValue().get()
                .asString();
    }

    public String getStartDateFromEvent(ApplicationCommandInteractionEvent event) {
        Optional<ApplicationCommandInteractionOption> startDateOptional = event.getInteraction().getCommandInteraction().get()
                .getOption("start_date");

        if (startDateOptional.isEmpty()) return null;

        return event.getInteraction().getCommandInteraction().get().
                getOption("start_date").get()
                .getValue().get()
                .asString();
    }

    public String getEndDateFromEvent(ApplicationCommandInteractionEvent event) {
        Optional<ApplicationCommandInteractionOption> startDateOptional = event.getInteraction().getCommandInteraction().get()
                .getOption("start_date");

        if (startDateOptional.isEmpty()) return null;

        return event.getInteraction().getCommandInteraction().get().
                getOption("end_date").get()
                .getValue().get()
                .asString();
    }

    public String getImpliedStartString(ApplicationCommandInteractionEvent event) {
        String startDate = getStartDateFromEvent(event);

        if (startDate == null) {
            return LocalDate.now().toString();
        }

        if (!StringUtil.isValidDate(startDate)) return null;

        return startDate;
    }

    public String getImpliedEndString(ApplicationCommandInteractionEvent event) {
        String endDate = getEndDateFromEvent(event);

        if (endDate == null) {
            return LocalDate.now().minusMonths(1).toString();
        }

        if (!StringUtil.isValidDate(endDate)) return null;

        return endDate;
    }

    public String getTeamNameFromId(String teamId) {
        String token = TokenRegistry.getFaceitToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        String data = ApiHandler.getRequest(TEAM_ID_ENDPOINT + teamId, headers, null);

        if (data.equals("")) return null;

        try {
            Map<String, Object> map = JsonManager.readJson(data);

            return (String) map.getOrDefault("name", null);
        } catch(JsonProcessingException e) {
            return null;
        }
    }

    public void onPythonError(ApplicationCommandInteractionEvent event, String teamId, String teamName) {
        String logFilePath = PythonRunner.WINS_LOG_PATH;

        PythonErrorEmbed embed = new PythonErrorEmbed(teamId, teamName, "Wins", logFilePath);
        List<EmbedCreateSpec> embeds = new ArrayList<>();
        embeds.add(embed.getEmbed());

        InteractionReplyEditSpec msg = InteractionReplyEditSpec.builder()
                .embeds(embeds)
                .build();
        event.editReply(msg).subscribe();
    }

    public void onDateError(ApplicationCommandInteractionEvent event, String date, String dateType) {
        DateError embed = new DateError(date, dateType);
        List<EmbedCreateSpec> embeds = new ArrayList<>();
        embeds.add(embed.getEmbed());

        InteractionReplyEditSpec msg = InteractionReplyEditSpec.builder()
                .embeds(embeds)
                .build();
        event.editReply(msg).subscribe();
    }
}
