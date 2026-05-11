package com.kjersti.astryx.sql.data;

import com.kjersti.astryx.api.handlers.VetoDataHandler;
import com.kjersti.astryx.api.object.ChampionshipObject;
import com.kjersti.astryx.api.object.VetoObject;
import com.kjersti.astryx.common.logging.AstryxLogManager;
import com.kjersti.astryx.common.registry.SettingRegistry;
import com.kjersti.astryx.common.util.ActionObject;
import com.kjersti.astryx.common.util.Duration;
import com.kjersti.astryx.sql.SqlBuilder;
import com.kjersti.astryx.sql.SqlHandler;
import com.kjersti.astryx.sql.SqlObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.validators.ValidHostValidator;

import java.util.ArrayList;
import java.util.List;

public class ProcessRawVetoData {
    public static final Logger LOGGER = AstryxLogManager.getLogger("veto");

    public static void processRawVetoData() {
        LOGGER.info("Processing veto raw data");
        long startTime = System.nanoTime();

        List<String> matchIds = getUniqueMatches();
        List<VetoObject> processedVetoes = processVetoes(matchIds);
        addProcessedVetoesToDatabase(processedVetoes);

        long endTime = System.nanoTime();
        Duration duration = new Duration(startTime, endTime);
        int min = duration.getMinutes();
        int sec =  duration.getSeconds();

        LOGGER.info("Processed {} vetoes. Execution time {} minutes, {} seconds",
                processedVetoes.size(), min, sec);
    }

    public static List<VetoObject> getVetoesFromMatchId(String matchId) {
        String rawVetoDataTable = SettingRegistry.getRawVetoTable();

        String query = "SELECT * FROM " + rawVetoDataTable +
                " WHERE `match_id` = '" + matchId +
                "' ORDER BY `veto_index` ASC;";

        SqlObject obj = SqlHandler.executeApiQuery(query);
        List<VetoObject> vetoes = new ArrayList<>();

        for (String[] row: obj.getDataWithoutColumnNames()) {
            VetoObject vetoObject = new VetoObject(row);

            vetoes.add(vetoObject);
        }

        return vetoes;
    }

    public static List<String> getUniqueMatches() {
        String rawVetoDataTable = SettingRegistry.getRawVetoTable();
        String query = "SELECT DISTINCT `match_id` FROM " + rawVetoDataTable + ";";

        SqlObject matches = SqlHandler.executeApiQuery(query);

        List<String> matchIds = new ArrayList<>();

        for (String[] row: matches.getDataWithoutColumnNames()) {
            matchIds.add(row[0]);
        }

        return matchIds;
    }

    public static List<List<VetoObject>> partitionVetoes(List<VetoObject> matchVetoes) {
        List<List<VetoObject>> partitionedVetoes = new ArrayList<>();

        for (VetoObject veto: matchVetoes) {
            int round = veto.getRound();

            if (round == 1) {
                List<VetoObject> newGroup = new ArrayList<>();

                newGroup.add(veto);
                partitionedVetoes.add(newGroup);
            } else {
                int index = partitionedVetoes.size()-1;

                partitionedVetoes.get(index).add(veto);
            }
        }

        return partitionedVetoes;
    }

    public static List<VetoObject> processVetoes(List<String> matchIds) {
        List<VetoObject> allVetoes = new ArrayList<>();

        for (String matchId: matchIds) {
            List<VetoObject> vetoes = getVetoesFromMatchId(matchId);
            List<List<VetoObject>> partitionedVetoes = partitionVetoes(vetoes);

            for (List<VetoObject> group: partitionedVetoes) {
                VetoObject pickVeto = group.get(group.size()-2);
                VetoObject dropVeto = group.get(group.size()-1);

                VetoObject finalVeto = new VetoObject(pickVeto, dropVeto);

                for (int i = 0; i < group.size() - 2; i++) {
                    allVetoes.add(group.get(i));
                }

                allVetoes.add(finalVeto);
            }
        }

        return allVetoes;
    }

    public static void addProcessedVetoesToDatabase(List<VetoObject> processedVetoes) {
        if (processedVetoes.isEmpty()) return;

        int batchSize = SettingRegistry.getSqlBatchLimit();
        int totalSize = processedVetoes.size();

        int numberOfBatches = processedVetoes.size()/batchSize + 1;

        if (numberOfBatches > 1) {
            LOGGER.info("Object count more than maximum (" + batchSize + "). Using "
                    + numberOfBatches + " batches (" + totalSize + " total)");
        }

        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);

            List<VetoObject> batch = processedVetoes.subList(start, end);
            executeBatch(batch);
        }
    }

    private static void executeBatch(List<VetoObject> batch) {
        String command = SqlBuilder.addProcessedMatchVetos(batch);

        SqlHandler.executeApiUpdate(command);
    }
}
