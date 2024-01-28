package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lol.stats.configuration.ApiKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Service
class RiotFacade {
    private final FeignRiotSummonerInfoEUN1 feignRiotSummonerInfoEUN1;
    private final FeignRiotAllChampion feignRiotAllChampion;
    private final FeignRiotMatches feignRiotMatches;
    private final FeignLolVersion feignLolVersion;
    private final ApiKeyProvider apiKeyProvider;

    @Value("${masala.puuid}")
    private String MASALA_PUUID;

    @Value("${ddragon.url}")
    private String ICON_URL;


    RiotFacade(FeignRiotSummonerInfoEUN1 feignRiotSummonerInfoEUN1, FeignRiotAllChampion feignRiotAllChampion, FeignRiotMatches feignRiotMatches, FeignLolVersion feignLolVersion, ApiKeyProvider apiKeyProvider) {
        this.feignRiotSummonerInfoEUN1 = feignRiotSummonerInfoEUN1;
        this.feignRiotAllChampion = feignRiotAllChampion;
        this.feignRiotMatches = feignRiotMatches;
        this.feignLolVersion = feignLolVersion;
        this.apiKeyProvider = apiKeyProvider;
    }

    JsonNode getSummonerInfoByName(final String summonerName) throws InterruptedException {
        ObjectNode summonerInfo = (ObjectNode) getSummonerInfo(summonerName);

        ArrayNode rank = getSummonerRank(summonerInfo.get("id").asText());
        summonerInfo.put("ranks", rank);

        JsonNode championsInfo = feignRiotSummonerInfoEUN1.getMainChampions(summonerInfo.get("puuid").asText(), apiKeyProvider.provideKey());
        getMainChampion(summonerInfo, championsInfo);
        setRanksForSoloAndFlex(rank, summonerInfo);

        String iconUrl = getProfileIconUrl(summonerInfo.get("profileIconId").asText());
        summonerInfo.put("iconUrl", iconUrl);

        if (summonerName.equalsIgnoreCase("ziomekmasala"))
            summonerInfo.put("name", "ZiomekMasala");

        return summonerInfo;
    }

    private void setRanksForSoloAndFlex(ArrayNode rank, ObjectNode summonerInfo) {
        if (rank != null) {
            for (JsonNode n : rank) {
                if (n.get("rankFlex") != null && !n.get("rankFlex").isEmpty() &&
                        n.get("rankFlex").get("queueType") != null && n.get("rankFlex").get("queueType").asText().equals("RANKED_FLEX_SR")) {
                    summonerInfo.put("rankFlexColor", setRankColorDependsOnRank(n.get("rankFlex").get("tier").asText()));

                } else if (n.get("rankSolo") != null && !n.get("rankSolo").isEmpty() &&
                        n.get("rankSolo").get("queueType") != null && n.get("rankSolo").get("queueType").asText().equals("RANKED_SOLO_5x5")) {
                    summonerInfo.put("rankSoloColor", setRankColorDependsOnRank(n.get("rankSolo").get("tier").asText()));
                }
            }
        }
    }

    private String getProfileIconUrl(String summonerIconId) {
        return ICON_URL + getLatestRiotVersion() + "/img/profileicon/" + summonerIconId + ".png";
    }

    private void getLastRankedMatchesDependsOnCount(ObjectNode summonerInfo, JsonNode matchesInfo, int count) throws InterruptedException {
        int matches = 0;
        int questions = 0;
        int wins = 0;
        int losses = 0;
        ArrayNode matchesArray = JsonNodeFactory.instance.arrayNode();

        for (JsonNode singleMatch : matchesInfo) {
            JsonNode match = getInfoAboutMatchById(singleMatch.asText());
            if (match.get("gameMode").asText().equals("CLASSIC")) {
                for (JsonNode m : match.get("participants")) {
                    if (m.get("puuid").asText().equals(summonerInfo.get("puuid").asText())) {
                        ObjectNode temp = JsonNodeFactory.instance.objectNode();
                        temp.put("matchChampName", m.get("championName"));
                        temp.put("assists", m.get("assists"));
                        temp.put("kda", m.get("challenges").get("kda"));
                        temp.put("deaths", m.get("deaths"));
                        temp.put("kills", m.get("kills"));
                        temp.put("lane", m.get("teamPosition"));
                        temp.put("dealtDamage", m.get("totalDamageDealtToChampions"));
                        switch (m.get("win").asText()) {
                            case "true" -> {
                                temp
                                        .put("win", "WYGRANA")
                                        .put("winColor", "green");
                                wins++;
                            }

                            case "false" -> {
                                temp
                                        .put("win", "PRZEGRANA")
                                        .put("winColor", "red");
                                losses++;
                            }
                        }
                        matchesArray.add(temp);
                        matches++;
                        if (matches >= count) {
                            summonerInfo.put("wins", wins);
                            summonerInfo.put("losses", losses);
                            summonerInfo.put("matches", matchesArray);
                            return;
                        }
                        break;
                    }
                }
            }
            questions++;
            if (questions >= 15) {
                sleep(1000);
                questions = 0;
            }
        }
    }

    private void getMainChampion(ObjectNode obj, JsonNode summonerInfo) {
        for (JsonNode champion : summonerInfo) {
            obj.put("mainChamp", getChampionById(champion.get("championId").asText()));
        }
    }

    private ArrayNode getSummonerRank(final String summonerId) {
        JsonNode jsonNode = feignRiotSummonerInfoEUN1.getLeagueV4(summonerId, apiKeyProvider.provideKey());
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        if (jsonNode != null && !jsonNode.isEmpty()) {
            for (JsonNode rank : jsonNode) {
                ObjectNode league = JsonNodeFactory.instance.objectNode();
                if (rank.get("queueType").asText().equals("RANKED_FLEX_SR")) {
                    league.put("rankFlex", rank);
                    arrayNode.add(league);
                } else if (rank.get("queueType").asText().equals("RANKED_SOLO_5x5")) {
                    league.put("rankSolo", rank);
                    arrayNode.add(league);
                } else {
                    league.put("rankFlex", "BRAK RANGI");
                    league.put("rankSolo", "BRAK RANGI");
                }
            }
        }
        return arrayNode;
    }

    private String setRankColorDependsOnRank(final String rank) {

        String lowerCaseRank = rank.toLowerCase();
        if (lowerCaseRank.contains("gold")) {
            return "#FFD700";
        } else if (lowerCaseRank.contains("silver")) {
            return "#C0C0C0";
        } else if (lowerCaseRank.contains("platinum")) {
            return "#A9A9A9";
        } else if (lowerCaseRank.contains("emerald")) {
            return "#2ecc71";
        } else if (lowerCaseRank.contains("diamond")) {
            return "#00CED1";
        } else if (lowerCaseRank.contains("bronze")) {
            return "#964B00";
        } else if (lowerCaseRank.contains("grandmaster")) {
            return "#000080";
        } else if (lowerCaseRank.contains("master")) {
            return "#800080";
        } else {
            return "#363949";
        }
    }

    private String getLatestRiotVersion() {
        return feignLolVersion.getLolVersions().get(0).asText();
    }

    private JsonNode getSummonerInfo(String summonerName) {
        if (summonerName.equalsIgnoreCase("ziomekmasala")) {
            return feignRiotSummonerInfoEUN1.getSummonerByPuuid(MASALA_PUUID, apiKeyProvider.provideKey());
        }
        return feignRiotSummonerInfoEUN1.getSummonerByName(summonerName, apiKeyProvider.provideKey());
    }

    String getChampionById(String championId) {
        if (championId.equals("-1")) {
            return "BRAK";
        }
        String latestVersion = feignRiotAllChampion.getLolVersions()[0];
        String championName = getChampionByKey(championId, feignRiotAllChampion.getChampionById(latestVersion).get("data")).get("name").asText();
        return championName != null ? championName.replaceAll("[\\s'.]+", "") : "Brak takiego championka";
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

    JsonNode getSummonerMatchesByNameAndCount(String summonerName, int count) {
        String puuId = getSummonerInfo(summonerName).get("puuid").asText();
        return feignRiotMatches.getMatchesByPuuidAndCount(puuId, count, apiKeyProvider.provideKey());
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return feignRiotMatches.getInfoAboutMatchById(matchId, apiKeyProvider.provideKey()).get("info");
    }

    JsonNode getInfoAboutAllSummonerInActiveGame(String summonerName) {
        JsonNode summonerInfo = getSummonerInfo(summonerName);
        JsonNode matchInfo = feignRiotSummonerInfoEUN1.getMatchInfoBySummonerId(summonerInfo.get("id").asText(), apiKeyProvider.provideKey());
        ObjectNode allInfoAboutMatch = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ArrayNode bannedChampionsArray = JsonNodeFactory.instance.arrayNode();

        if (!matchInfo.isEmpty() && !matchInfo.get("participants").isEmpty()) {
            String userTeam = null;
            for (JsonNode s : matchInfo.get("participants")) {
                ObjectNode summoner = (ObjectNode) s;
                ArrayNode ranks = getSummonerRank(s.get("summonerId").asText());
                setRankedSoloRank(ranks, summoner);
                summoner.put("champName", getChampionById(s.get("championId").asText()));
                summoner.put("1spellName", getSpellNameBySpellId(s.get("spell1Id").asText()));
                summoner.put("2spellName", getSpellNameBySpellId(s.get("spell2Id").asText()));
                arrayNode.add(summoner);

                if (s.get("summonerId").asText().equals(summonerInfo.get("id").asText()))
                    userTeam = summoner.get("teamId").asText();
            }
            allInfoAboutMatch.put("summoners", arrayNode);

            for (JsonNode champ : matchInfo.get("bannedChampions")) {
                bannedChampionsArray.add(getChampionById(champ.get("championId").asText()));
            }

            allInfoAboutMatch.put("bannedChampions", bannedChampionsArray);
            allInfoAboutMatch.put("userTeam", userTeam);
            allInfoAboutMatch.put("gameMode", matchInfo.get("gameMode"));
        }
        return allInfoAboutMatch;
    }

    private void setRankedSoloRank(ArrayNode ranks, ObjectNode summoner) {
        if (ranks != null && !ranks.isEmpty()) {
            for (JsonNode r : ranks) {
                if (r.get("rankSolo") != null && r.get("rankSolo").get("queueType").asText().equals("RANKED_SOLO_5x5")) {
                    summoner.put("rank", r.get("rankSolo").get("tier"));
                    summoner.put("rankColor", setRankColorDependsOnRank(r.get("rankSolo").get("tier").asText()));
                    return;
                } else {
                    summoner.put("rank", "BRAK RANGI");
                    summoner.put("rankColor", "#363949");
                }
            }
        } else {
            summoner.put("rank", "BRAK RANGI");
            summoner.put("rankColor", "#363949");
        }
    }

    private String getSpellNameBySpellId(String spellId) {
        JsonNode summonerSpells = feignRiotAllChampion.getSummonerSpells(getLatestRiotVersion());

        for (JsonNode n : summonerSpells.get("data")) {
            if (n.get("key").asText().equals(spellId)) {
                return n.get("name").asText();
            }
        }
        return "";
    }

    JsonNode getLast10MatchesBySummonerName(String summonerName) throws InterruptedException {
        JsonNode matchesInfo = getSummonerMatchesByNameAndCount(summonerName, 20);
        ObjectNode summonerInfo = getLeagueInfoFromMatchesList(summonerName);
        getLastRankedMatchesDependsOnCount(summonerInfo, matchesInfo, 3);
        return summonerInfo;
    }

    private JsonNode getLeagueInfo(String summonerId) {
        JsonNode leagueInfo = feignRiotSummonerInfoEUN1.getLeagueInfoBySummonerId(summonerId, apiKeyProvider.provideKey());
        if (leagueInfo != null && !leagueInfo.isEmpty()) {
            for (JsonNode info : leagueInfo) {
                if (info.get("queueType").asText().equals("RANKED_SOLO_5x5")) {
                    return info;
                }
            }
        }
        return leagueInfo;
    }

    public String getRandomSummonerNameFromExistingGame() {
        JsonNode exampleMatch = feignRiotSummonerInfoEUN1.getExampleSummonerNameFromRandomExistingGame(apiKeyProvider.provideKey());
        if (exampleMatch != null && !exampleMatch.get("gameList").isEmpty()) {
            String summonerNameFromExistingGame = exampleMatch.get("gameList").get(0).get("participants").get(0).get("summonerName").asText();
            return summonerNameFromExistingGame != null ? summonerNameFromExistingGame : "Spróbuj ponownie za chwilę";
        }
        return "Spróbuj ponownie za chwilę";
    }

    public JsonNode getLast20MatchesBySummonerName(String summonerName) throws InterruptedException {
        JsonNode matchesInfo = getSummonerMatchesByNameAndCount(summonerName, 50);
        ObjectNode summonerInfo = getLeagueInfoFromMatchesList(summonerName);
        getLastRankedMatchesDependsOnCount(summonerInfo, matchesInfo, 20);
        return summonerInfo;
    }

    private ObjectNode getLeagueInfoFromMatchesList(String summonerName) {
        ObjectNode summonerInfo = (ObjectNode) getSummonerInfo(summonerName);
        String summonerId = summonerInfo.get("id").asText();
        ArrayNode ranks = getSummonerRank(summonerId);
        JsonNode leagueInfo = getLeagueInfo(summonerId);
        if (leagueInfo != null) {
            summonerInfo.put("leagueInfo", leagueInfo);
        }
        setRankedSoloRank(ranks, summonerInfo);
        return summonerInfo;
    }
}
