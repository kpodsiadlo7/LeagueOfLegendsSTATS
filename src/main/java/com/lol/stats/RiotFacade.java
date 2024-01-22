package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
class RiotFacade {
    private final FeignRiotSummonerInfo feignRiotSummonerInfo;
    private final FeignRiotAllChampion feignRiotAllChampion;
    private final FeignRiotMatches feignRiotMatches;

    @Value("${masala.puuid}")
    private String MASALA_PUUID;

    RiotFacade(FeignRiotSummonerInfo feignRiotSummonerInfo, FeignRiotAllChampion feignRiotAllChampion, FeignRiotMatches feignRiotMatches) {
        this.feignRiotSummonerInfo = feignRiotSummonerInfo;
        this.feignRiotAllChampion = feignRiotAllChampion;
        this.feignRiotMatches = feignRiotMatches;
    }

    JsonNode getSummonerInfoByName(final String summonerName) {
        if(summonerName.equalsIgnoreCase("ziomekmasala")){
            return feignRiotSummonerInfo.getSummonerByPuuid(MASALA_PUUID);
        }
        return feignRiotSummonerInfo.getSummonerByName(summonerName);
    }

    String getChampionById(String championId) {
        String latestVersion = feignRiotAllChampion.getLolVersions()[0];
        JsonNode champion = getChampionByKey(championId,feignRiotAllChampion.getChampionById(latestVersion).get("data"));
        return champion != null ? champion.get("name").toString() : "Brak takiego championka";
    }

    private static JsonNode getChampionByKey(String key, JsonNode champions) {

        for (JsonNode championNode : champions) {
            String championKey = championNode.get("key").asText();

            if (championKey.equals(key)) {
                return championNode;
            }
        }
        return null;
    }

    JsonNode getAllSummonerMatchesByName(String summonerName, int count) {
        String puuid = getSummonerInfoByName(summonerName).get("puuid").asText();
        return feignRiotMatches.getAllMatchesByPuuidAndCount(puuid, count);
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return feignRiotMatches.getInfoAboutMatchById(matchId).get("info").get("participants");
    }
}
