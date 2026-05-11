package com.kjersti.astryx.sql;

import com.kjersti.astryx.api.object.*;
import com.kjersti.astryx.common.registry.SettingRegistry;

import java.util.List;

public class SqlBuilder {
    public static String buildAddChampionshipCommand(List<ChampionshipObject> objects) {
        String championshipDataTable = SettingRegistry.getChampionshipDataTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        championshipDataTable +
                        "` (`id`, `name`, `type`, `status`, `region`, `checkin_start`, `slots`, `join_policy`, `rounds`, `groups`, `offset`, `archived`) VALUES "
        );

        for (int i = 0; i < objects.size(); i++) {
            ChampionshipObject obj = objects.get(i);
            command.append("(")
                    .append("\"").append(obj.getId()).append("\", ")
                    .append("\"").append(obj.getName()).append("\", ")
                    .append("\"").append(obj.getType()).append("\", ")
                    .append("\"").append(obj.getStatus()).append("\", ")
                    .append("\"").append(obj.getRegion()).append("\", ")
                    .append(obj.getCheckinStart()).append(", ")
                    .append(obj.getSlots()).append(", ")
                    .append("\"").append(obj.getJoinPolicy()).append("\", ")
                    .append(obj.getTotalRounds()).append(", ")
                    .append(obj.getTotalGroups()).append(", ")
                    .append(obj.getOffset()).append(", ")
                    .append(false)
                    .append(")");

            if (i < objects.size() - 1) {
                command.append(", ");
            }
        }

        command.append(" ON DUPLICATE KEY UPDATE " +
                "`name` = VALUES(`name`), " +
                "`type` = VALUES(`type`), " +
                "`status` = VALUES(`status`), " +
                "`region` = VALUES(`region`), " +
                "`checkin_start` = VALUES(`checkin_start`), " +
                "`slots` = VALUES(`slots`), " +
                "`join_policy` = VALUES(`join_policy`), " +
                "`rounds` = VALUES(`rounds`), " +
                "`groups` = VALUES(`groups`), " +
                "`offset` = VALUES(`offset`), " +
                "`archived` = VALUES(`archived`);");

        return command.toString();
    }

    public static String getStartingChampionshipOffset() {
        String championshipTable = SettingRegistry.getChampionshipDataTable();

        return "SELECT `offset` FROM `" +
                championshipTable +
                "` WHERE `status` != 'finished' ORDER BY `offset` ASC LIMIT 1;";
    }

    public static String getFinalChampionshipOffset() {
        String championshipTable = SettingRegistry.getChampionshipDataTable();

        return "SELECT `offset` FROM `" +
                championshipTable +
                "` WHERE `status` != 'finished' ORDER BY `offset` DESC LIMIT 1;";
    }

    public static String getFinishedChampionships() {
        String championshipTable = SettingRegistry.getChampionshipDataTable();

        return "SELECT `id` FROM `" +
                championshipTable +
                "` WHERE `status` = 'finished' AND `archived`=0;";
    }

    public static String addToMapDataTable(List<MapObject> toAdd) {
        String table = SettingRegistry.getFaceitMapDataTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        table +
                        "` (`date`, `match_id`, `match_round`, `mode`, `map`, `team1_id`, `team1_score`, `team1_name`, " +
                        "`team2_id`, `team2_score`, `team2_name`, `winner_id`, `winner_name`, `loser_id`, " +
                        "`loser_name`, `player1_id`, `player1_nickname`, `player1_role`, `player1_eliminations`, " +
                        "`player1_assists`, `player1_deaths`, `player1_damage_dealt`, `player1_healing_done`, " +
                        "`player1_damage_mitigated`, `player2_id`, `player2_nickname`, `player2_role`, " +
                        "`player2_eliminations`, `player2_assists`, `player2_deaths`, `player2_damage_dealt`, " +
                        "`player2_healing_done`, `player2_damage_mitigated`, `player3_id`, `player3_nickname`, " +
                        "`player3_role`, `player3_eliminations`, `player3_assists`, `player3_deaths`, " +
                        "`player3_damage_dealt`, `player3_healing_done`, `player3_damage_mitigated`, " +
                        "`player4_id`, `player4_nickname`, `player4_role`, `player4_eliminations`, " +
                        "`player4_assists`, `player4_deaths`, `player4_damage_dealt`, " + "`player4_healing_done`, " +
                        "`player4_damage_mitigated`, `player5_id`, `player5_nickname`, `player5_role`, " +
                        "`player5_eliminations`, `player5_assists`, `player5_deaths`, `player5_damage_dealt`, " +
                        "`player5_healing_done`, `player5_damage_mitigated`, `player6_id`, `player6_nickname`, " +
                        "`player6_role`, `player6_eliminations`, `player6_assists`, `player6_deaths`, " +
                        "`player6_damage_dealt`, `player6_healing_done`, `player6_damage_mitigated`, " +
                        "`player7_id`, `player7_nickname`, `player7_role`, `player7_eliminations`, `player7_assists`, " +
                        "`player7_deaths`, `player7_damage_dealt`, `player7_healing_done`, `player7_damage_mitigated`, " +
                        "`player8_id`, `player8_nickname`, `player8_role`, `player8_eliminations`, `player8_assists`, " +
                        "`player8_deaths`, `player8_damage_dealt`, `player8_healing_done`, `player8_damage_mitigated`, " +
                        "`player9_id`, `player9_nickname`, `player9_role`, `player9_eliminations`, `player9_assists`, " +
                        "`player9_deaths`, `player9_damage_dealt`, `player9_healing_done`, `player9_damage_mitigated`, " +
                        "`player10_id`, `player10_nickname`, `player10_role`, `player10_eliminations`, " +
                        "`player10_assists`, `player10_deaths`, `player10_damage_dealt`, `player10_healing_done`, " +
                        "`player10_damage_mitigated`) VALUES "
        );

        for (int i = 0; i < toAdd.size(); i++) {
            MapObject obj = toAdd.get(i);
            String objSqlValue = obj.getSqlInsertString();

            command.append(objSqlValue);

            if (i < toAdd.size() - 1) {
                command.append(", ");
            }
        }

        command.append("ON DUPLICATE KEY UPDATE " +
                "`date` = VALUES(`date`), " +
                "`mode` = VALUES(`mode`), " +
                "`map` = VALUES(`map`), " +
                "`team1_score` = VALUES(`team1_score`), " +
                "`team2_score` = VALUES(`team2_score`), " +
                "`winner_id` = VALUES(`winner_id`), " +
                "`winner_name` = VALUES(`winner_name`), " +
                "`loser_id` = VALUES(`loser_id`), " +
                "`loser_name` = VALUES(`loser_name`), " +
                "`player1_eliminations` = VALUES(`player1_eliminations`), " +
                "`player1_assists` = VALUES(`player1_assists`), " +
                "`player1_deaths` = VALUES(`player1_deaths`), " +
                "`player1_damage_dealt` = VALUES(`player1_damage_dealt`), " +
                "`player1_healing_done` = VALUES(`player1_healing_done`), " +
                "`player1_damage_mitigated` = VALUES(`player1_damage_mitigated`), " +
                "`player2_eliminations` = VALUES(`player2_eliminations`), " +
                "`player2_assists` = VALUES(`player2_assists`), " +
                "`player2_deaths` = VALUES(`player2_deaths`), " +
                "`player2_damage_dealt` = VALUES(`player2_damage_dealt`), " +
                "`player2_healing_done` = VALUES(`player2_healing_done`), " +
                "`player2_damage_mitigated` = VALUES(`player2_damage_mitigated`), " +
                "`player3_eliminations` = VALUES(`player3_eliminations`), " +
                "`player3_assists` = VALUES(`player3_assists`), " +
                "`player3_deaths` = VALUES(`player3_deaths`), " +
                "`player3_damage_dealt` = VALUES(`player3_damage_dealt`), " +
                "`player3_healing_done` = VALUES(`player3_healing_done`), " +
                "`player3_damage_mitigated` = VALUES(`player3_damage_mitigated`), " +
                "`player4_eliminations` = VALUES(`player4_eliminations`), " +
                "`player4_assists` = VALUES(`player4_assists`), " +
                "`player4_deaths` = VALUES(`player4_deaths`), " +
                "`player4_damage_dealt` = VALUES(`player4_damage_dealt`), " +
                "`player4_healing_done` = VALUES(`player4_healing_done`), " +
                "`player4_damage_mitigated` = VALUES(`player4_damage_mitigated`), " +
                "`player5_eliminations` = VALUES(`player5_eliminations`), " +
                "`player5_assists` = VALUES(`player5_assists`), " +
                "`player5_deaths` = VALUES(`player5_deaths`), " +
                "`player5_damage_dealt` = VALUES(`player5_damage_dealt`), " +
                "`player5_healing_done` = VALUES(`player5_healing_done`), " +
                "`player5_damage_mitigated` = VALUES(`player5_damage_mitigated`), " +
                "`player6_eliminations` = VALUES(`player6_eliminations`), " +
                "`player6_assists` = VALUES(`player6_assists`), " +
                "`player6_deaths` = VALUES(`player6_deaths`), " +
                "`player6_damage_dealt` = VALUES(`player6_damage_dealt`), " +
                "`player6_healing_done` = VALUES(`player6_healing_done`), " +
                "`player6_damage_mitigated` = VALUES(`player6_damage_mitigated`), " +
                "`player7_eliminations` = VALUES(`player7_eliminations`), " +
                "`player7_assists` = VALUES(`player7_assists`), " +
                "`player7_deaths` = VALUES(`player7_deaths`), " +
                "`player7_damage_dealt` = VALUES(`player7_damage_dealt`), " +
                "`player7_healing_done` = VALUES(`player7_healing_done`), " +
                "`player7_damage_mitigated` = VALUES(`player7_damage_mitigated`), " +
                "`player8_eliminations` = VALUES(`player8_eliminations`), " +
                "`player8_assists` = VALUES(`player8_assists`), " +
                "`player8_deaths` = VALUES(`player8_deaths`), " +
                "`player8_damage_dealt` = VALUES(`player8_damage_dealt`), " +
                "`player8_healing_done` = VALUES(`player8_healing_done`), " +
                "`player8_damage_mitigated` = VALUES(`player8_damage_mitigated`)," +
                "`player9_eliminations` = VALUES(`player9_eliminations`), " +
                "`player9_assists` = VALUES(`player9_assists`), " +
                "`player9_deaths` = VALUES(`player9_deaths`), " +
                "`player9_damage_dealt` = VALUES(`player9_damage_dealt`), " +
                "`player9_healing_done` = VALUES(`player9_healing_done`), " +
                "`player9_damage_mitigated` = VALUES(`player9_damage_mitigated`), " +
                "`player10_eliminations` = VALUES(`player10_eliminations`), " +
                "`player10_assists` = VALUES(`player10_assists`), " +
                "`player10_deaths` = VALUES(`player10_deaths`), " +
                "`player10_damage_dealt` = VALUES(`player10_damage_dealt`), " +
                "`player10_healing_done` = VALUES(`player10_healing_done`), " +
                "`player10_damage_mitigated` = VALUES(`player10_damage_mitigated`);");

        return command.toString();
    }

    public static String addRawMatchVetos(List<VetoObject> objects) {
        String vetoDataTable = SettingRegistry.getRawVetoTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        vetoDataTable +
                        "` (`date`, `veto_index`, `match_id`, `team1_id`, `team2_id`, `guid`, " +
                        "`status`, `random`, `round`, `selected_by`) VALUES "
        );

        for (int i = 0; i < objects.size(); i++) {
            VetoObject obj = objects.get(i);

            int random = 0;

            if (obj.isRandom()) random = 1;

            command.append("(")
                    .append("\"").append(obj.getDate()).append("\", ")
                    .append("\"").append(obj.getVetoIndex()).append("\", ")
                    .append("\"").append(obj.getMatchId()).append("\", ")
                    .append("\"").append(obj.getTeam1Id()).append("\", ")
                    .append("\"").append(obj.getTeam2Id()).append("\", ")
                    .append("\"").append(obj.getGuid()).append("\", ")
                    .append("\"").append(obj.getStatus()).append("\", ")
                    .append("\"").append(random).append("\", ")
                    .append(obj.getRound()).append(", ")
                    .append("\"").append(obj.getSelectedBy()).append("\"")
                    .append(")");

            if (i < objects.size() - 1) {
                command.append(", ");
            }
        }

        command.append(" ON DUPLICATE KEY UPDATE `date`=VALUES(`date`), " +
                "`team1_id`=VALUES(`team1_id`), " +
                "`team2_id`=VALUES(`team2_id`), " +
                "`guid`=VALUES(`guid`), " +
                "`status`=VALUES(`status`), " +
                "`random`=VALUES(`random`), " +
                "`round`=VALUES(`round`), " +
                "`selected_by`=VALUES(`selected_by`);"
        );

        return command.toString();
    }

    public static String addProcessedMatchVetos(List<VetoObject> objects) {
        String vetoDataTable = SettingRegistry.getVetoDataTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        vetoDataTable +
                        "` (`date`, `veto_index`, `match_id`, `team1_id`, `team2_id`, `guid`, " +
                        "`status`, `random`, `round`, `selected_by`) VALUES "
        );

        for (int i = 0; i < objects.size(); i++) {
            VetoObject obj = objects.get(i);

            int random = 0;

            if (obj.isRandom()) random = 1;

            command.append("(")
                    .append("\"").append(obj.getDate()).append("\", ")
                    .append("\"").append(obj.getVetoIndex()).append("\", ")
                    .append("\"").append(obj.getMatchId()).append("\", ")
                    .append("\"").append(obj.getTeam1Id()).append("\", ")
                    .append("\"").append(obj.getTeam2Id()).append("\", ")
                    .append("\"").append(obj.getGuid()).append("\", ")
                    .append("\"").append(obj.getStatus()).append("\", ")
                    .append("\"").append(random).append("\", ")
                    .append(obj.getRound()).append(", ")
                    .append("\"").append(obj.getSelectedBy()).append("\"")
                    .append(")");

            if (i < objects.size() - 1) {
                command.append(", ");
            }
        }

        command.append(" ON DUPLICATE KEY UPDATE `date`=VALUES(`date`), " +
                "`team1_id`=VALUES(`team1_id`), " +
                "`team2_id`=VALUES(`team2_id`), " +
                "`guid`=VALUES(`guid`), " +
                "`status`=VALUES(`status`), " +
                "`random`=VALUES(`random`), " +
                "`round`=VALUES(`round`), " +
                "`selected_by`=VALUES(`selected_by`);"
        );

        return command.toString();
    }

    public static String addProcessedH2HMatchVetos(List<H2HVetoObject> objects) {
        String vetoDataTable = SettingRegistry.getHeadToHeadTable();

        StringBuilder command = new StringBuilder(
                "INSERT INTO `" +
                        vetoDataTable +
                        "` (`date`, `match_id`, `team1_id`, `team2_id`, `picked_guid`, `dropped_guid`, " +
                        "`random`, `round`, `selected_by`) VALUES "
        );

        for (int i = 0; i < objects.size(); i++) {
            H2HVetoObject obj = objects.get(i);

            int random = 0;
            if (obj.isRandom()) random = 1;

            command.append("(")
                    .append("\"").append(obj.getDate()).append("\", ")
                    .append("\"").append(obj.getMatchId()).append("\", ")
                    .append("\"").append(obj.getTeam1Id()).append("\", ")
                    .append("\"").append(obj.getTeam2Id()).append("\", ")
                    .append("\"").append(obj.getPickedGuid()).append("\", ")
                    .append("\"").append(obj.getDroppedGuid()).append("\", ")
                    .append("\"").append(random).append("\", ")
                    .append(obj.getRound()).append(", ")
                    .append("\"").append(obj.getSelectedBy()).append("\"")
                    .append(")");

            if (i < objects.size() - 1) {
                command.append(", ");
            }
        }

        command.append(" ON DUPLICATE KEY UPDATE `date`=VALUES(`date`), " +
                "`team1_id`=VALUES(`team1_id`), " +
                "`team2_id`=VALUES(`team2_id`), " +
                "`dropped_guid`=VALUES(`dropped_guid`), " +
                "`random`=VALUES(`random`), " +
                "`round`=VALUES(`round`), " +
                "`selected_by`=VALUES(`selected_by`);"
        );

        return command.toString();
    }
}
