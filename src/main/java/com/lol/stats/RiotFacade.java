package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;


@Service
class RiotFacade {
    private final FeignRiotSummonerInfo feignRiotSummonerInfo;
    private final FeignRiotAllChampion feignRiotAllChampion;
    private final FeignRiotMatches feignRiotMatches;

    private final FeignLolVersion feignLolVersion;

    @Value("${masala.puuid}")
    private String MASALA_PUUID;

    @Value("${ddragon.url}")
    private String ICON_URL;


    RiotFacade(FeignRiotSummonerInfo feignRiotSummonerInfo, FeignRiotAllChampion feignRiotAllChampion, FeignRiotMatches feignRiotMatches, FeignLolVersion feignLolVersion) {
        this.feignRiotSummonerInfo = feignRiotSummonerInfo;
        this.feignRiotAllChampion = feignRiotAllChampion;
        this.feignRiotMatches = feignRiotMatches;
        this.feignLolVersion = feignLolVersion;
    }

    JsonNode getSummonerInfoByName(final String summonerName) {
        if(summonerName.equalsIgnoreCase("ziomekmasala")){
            System.out.println("pobrałem");
            return feignRiotSummonerInfo.getSummonerByPuuid(MASALA_PUUID);
        }
        return feignRiotSummonerInfo.getSummonerByName(summonerName);
    }

    String getChampionById(String championId) {
        String latestVersion = feignRiotAllChampion.getLolVersions()[0];
        JsonNode champion = getChampionByKey(championId,feignRiotAllChampion.getChampionById(latestVersion).get("data"));
        return champion != null ? champion.get("name").asText() : "Brak takiego championka";
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

    JsonNode getAllSummonerMatchesByName(String summonerName, int count) throws InterruptedException {
        String puuId = getSummonerInfo(summonerName).get("puuid").asText();
        return feignRiotMatches.getAllMatchesByPuuidAndCount(puuId, count);
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return feignRiotMatches.getInfoAboutMatchById(matchId).get("info");
    }
}
