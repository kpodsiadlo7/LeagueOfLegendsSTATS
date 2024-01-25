package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;


@Service
class RiotFacade {
    private final FeignRiotSummonerInfoEUN1 feignRiotSummonerInfoEUN1;
    private final FeignRiotAllChampion feignRiotAllChampion;
    private final FeignRiotMatches feignRiotMatches;

    private final FeignLolVersion feignLolVersion;

    @Value("${masala.puuid}")
    private String MASALA_PUUID;

    @Value("${ddragon.url}")
    private String ICON_URL;


    RiotFacade(FeignRiotSummonerInfoEUN1 feignRiotSummonerInfoEUN1, FeignRiotAllChampion feignRiotAllChampion, FeignRiotMatches feignRiotMatches, FeignLolVersion feignLolVersion) {
        this.feignRiotSummonerInfoEUN1 = feignRiotSummonerInfoEUN1;
        this.feignRiotAllChampion = feignRiotAllChampion;
        this.feignRiotMatches = feignRiotMatches;
        this.feignLolVersion = feignLolVersion;
    }

    JsonNode getSummonerInfoByName(final String summonerName) throws InterruptedException {
        ObjectNode summonerInfo = (ObjectNode) getSummonerInfo(summonerName);

        String rank = getSummonerRank(summonerInfo.get("id").asText());
        summonerInfo.put("rank", rank);

        JsonNode championsInfo = feignRiotSummonerInfoEUN1.getMainChampions(summonerInfo.get("puuid").asText());
        setThreeMainChampions(summonerInfo, championsInfo);

        JsonNode matchesInfo = getAllSummonerMatchesByName(summonerName,50);
        setThreeLastMatches(summonerInfo,matchesInfo);
        if(matchesInfo.isEmpty()) summonerInfo.put("win", "BRAK DANYCH");

        String rankColor = setRankColorDependsOnRank(rank);
        summonerInfo.put("rankColor", rankColor);

        String iconUrl = getProfileIconUrl(summonerInfo.get("profileIconId").asText());
        summonerInfo.put("iconUrl", iconUrl);

        if(summonerName.equalsIgnoreCase("ziomekmasala"))
            summonerInfo.put("name", "ZiomekMasala");

        return summonerInfo;
    }

    private String getProfileIconUrl(String summonerIconId) {
        return ICON_URL +  getLatestRiotVersion() + "/img/profileicon/" + summonerIconId + ".png";
    }

    private void setThreeLastMatches(ObjectNode summonerInfo, JsonNode matchesInfo) throws InterruptedException {
        int matches = 1;
        int questions = 0;
        for (JsonNode singleMatch : matchesInfo) {
            JsonNode match = getInfoAboutMatchById(singleMatch.asText());
               if(match.get("gameMode").asText().equals("CLASSIC")) {
                for (JsonNode m : match.get("participants")) {
                    if(m.get("puuid").asText().equals(summonerInfo.get("puuid").asText())) {
                        summonerInfo.put("matchChampName"+matches, m.get("championName"));
                        summonerInfo.put("assists"+matches, m.get("assists"));
                        summonerInfo.put("kda"+matches, m.get("challenges").get("kda"));
                        summonerInfo.put("multikills"+matches, m.get("challenges").get("multikills"));
                        summonerInfo.put("soloKills"+matches, m.get("challenges").get("soloKills"));
                        summonerInfo.put("deaths"+matches, m.get("deaths"));
                        summonerInfo.put("doubleKills"+matches, m.get("doubleKills"));
                        summonerInfo.put("kills"+matches, m.get("kills"));
                        summonerInfo.put("dealtDamage"+matches, m.get("totalDamageDealtToChampions"));
                        summonerInfo.put("tripleKills"+matches, m.get("tripleKills"));
                        summonerInfo.put("quadraKills"+matches, m.get("quadraKills"));
                        summonerInfo.put("killingSprees"+matches, m.get("killingSprees"));
                        switch (m.get("win").asText()) {
                            case "true" -> summonerInfo
                                    .put("win"+matches, "WYGRANA")
                                    .put("winColor"+matches, "green");

                            case "false" -> summonerInfo
                                    .put("win"+matches, "PRZEGRANA")
                                    .put("winColor"+matches, "red");
                        }
                        summonerInfo.put("pentaKills"+matches, m.get("pentaKills"));
                        matches++;
                        if(matches == 4)
                            return;
                    }
                }
            }
            questions++;
            if(questions >= 15){
                sleep(1000);
                questions = 0;
            }
        }
    }

    private void setThreeMainChampions(ObjectNode obj, JsonNode summonerInfo) {
        for(int i=1; i<=summonerInfo.size(); i++){
            obj.put("champ"+i,summonerInfo.get(i-1).get("championId"));
            obj.put("champLv"+i, summonerInfo.get(i-1).get("championLevel"));
            obj.put("champName"+i, getChampionById(summonerInfo.get(i-1).get("championId").asText()));
        }
    }

    private String getSummonerRank(final String id) {
        JsonNode jsonNode = feignRiotSummonerInfoEUN1.getLeagueV4(id);
        StringBuilder stringBuilder = new StringBuilder();
        for(JsonNode summoner: jsonNode) {
            if(summoner.get("queueType").asText().equals("RANKED_SOLO_5x5")) {
                return stringBuilder
                        .append(summoner.get("tier").asText())
                        .append("-")
                        .append(summoner.get("rank")
                                .asText()).toString();
            }
        }
        return "BRAK RANGI";
    }

    private String setRankColorDependsOnRank(final String rank) {
        String lowerCaseRank = rank.toLowerCase();

        if (lowerCaseRank.contains("gold")) {
            return "#FFD700";
        } else if (lowerCaseRank.contains("silver")) {
            return "#C0C0C0";
        } else if (lowerCaseRank.contains("platinum")) {
            return "#00CED1";
        } else if (lowerCaseRank.contains("emerald")) {
            return "#2ecc71";
        } else if (lowerCaseRank.contains("diamond")) {
            return "#00CED1";
        } else {
                return "#000000";
        }

        //TODO make cases for all ranks
    }

    private String getLatestRiotVersion() {
        return feignLolVersion.getLolVersions().get(0).asText();
    }

    private JsonNode getSummonerInfo(String summonerName) {
        if(summonerName.equalsIgnoreCase("ziomekmasala")){
            System.out.println("pobrałem");
            return feignRiotSummonerInfoEUN1.getSummonerByPuuid(MASALA_PUUID);
        }
        return feignRiotSummonerInfoEUN1.getSummonerByName(summonerName);
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

    JsonNode getInfoAboutAllSummonerInActiveGame(String summonerName) {
        System.out.println("getInfoAboutAllSummonerInActiveGame");
        JsonNode summonerInfo = getSummonerInfo(summonerName);
        JsonNode matchInfo = feignRiotSummonerInfoEUN1.getMatchInfoBySummonerId(summonerInfo.get("id").asText());
        ObjectNode allInfoAboutMatch = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ArrayNode bannedChampionsArray = JsonNodeFactory.instance.arrayNode();

        if(!matchInfo.isEmpty() && !matchInfo.get("participants").isEmpty()) {
            String userTeam = null;
            for (JsonNode s : matchInfo.get("participants")) {
                ObjectNode summoner = (ObjectNode) s;
                String rank = getSummonerRank(s.get("summonerId").asText());
                summoner.put("champName", getChampionById(s.get("championId").asText()));
                summoner.put("rank", rank);
                summoner.put("rankColor", setRankColorDependsOnRank(rank));
                summoner.put("1spellName", getSpellNameBySpellId(s.get("spell1Id").asText()));
                summoner.put("2spellName", getSpellNameBySpellId(s.get("spell2Id").asText()));
                arrayNode.add(summoner);

                if(s.get("summonerId").asText().equals(summonerInfo.get("id").asText()))
                    userTeam = summoner.get("teamId").asText();
            }
            allInfoAboutMatch.put("summoners", arrayNode);

            for(JsonNode champ: matchInfo.get("bannedChampions")){
                bannedChampionsArray.add(getChampionById(champ.get("championId").asText()));
            }

            allInfoAboutMatch.put("bannedChampions",bannedChampionsArray);
            allInfoAboutMatch.put("userTeam", userTeam);
            allInfoAboutMatch.put("gameMode", matchInfo.get("gameMode"));
        }
        return allInfoAboutMatch;
    }

    private String getSpellNameBySpellId(String spellId) {
        JsonNode summonerSpells = feignRiotAllChampion.getSummonerSpells(getLatestRiotVersion());

        for (JsonNode n : summonerSpells.get("data")){
            if(n.get("key").asText().equals(spellId)){
                return n.get("name").asText();
            }
        }
        return "X";
    }
}
