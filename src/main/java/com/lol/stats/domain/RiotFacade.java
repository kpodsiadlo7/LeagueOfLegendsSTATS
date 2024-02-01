package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.model.Champion;
import com.lol.stats.model.Rank;
import com.lol.stats.model.Summoner;
import com.lol.stats.model.SummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotFacade {

    private final AllChampionClient allChampionClient;

    private final Provider provider;

    public Summoner getSummonerInfoByName(final String summonerName) throws InterruptedException {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);

        List<Rank> ranks = getSummonerRank(summonerInfo.getId());
        Champion champion = getMainChampion(summonerInfo.getPuuid());
        String latestLoLVersion = getLatestLoLVersion();

        return bakeSummoner(summonerInfo,ranks,champion,latestLoLVersion);
    }
    //TODO ZAMIENIONE METODY
    private Summoner bakeSummoner(final SummonerInfo summonerInfo, final List<Rank> ranks, final Champion champion, final String latestLoLVersion) {
        Summoner summoner = setRanksForSoloAndFlex(ranks);
        String mainChampName = getChampionById(champion.getChampionId(),latestLoLVersion);

        return Summoner.builder()
                .id(summonerInfo.getId())
                .accountId(summonerInfo.getAccountId())
                .puuid(summonerInfo.getPuuid())
                .name(summonerInfo.getName())
                .profileIconId(summonerInfo.getProfileIconId())
                .summonerLevel(summonerInfo.getSummonerLevel())
                .ranks(ranks)
                .rankFlexColor(summoner.getRankFlexColor())
                .rankSoloColor(summoner.getRankSoloColor())
                .mainChamp(mainChampName)
                .versionLoL(latestLoLVersion)
                .build();
    }

    private Champion getMainChampion(final String puuid) {
        List<Champion> champions = provider.getChampionsByPuuId(puuid);
        if (champions != null && !champions.isEmpty()){
            return champions.get(0);
        }
        return new Champion();
    }

    private Summoner setRanksForSoloAndFlex(List<Rank> ranks) {
        String flexRankColor = "";
        String soloRankColor = "";
        if (ranks != null && !ranks.isEmpty()) {
            for (var rank : ranks) {
                if (rank.getQueueType().equals("RANKED_FLEX_SR")) {
                    soloRankColor = setRankColorDependsOnRank(rank.getTier());
                    log.warn(soloRankColor);

                } else if (rank.getQueueType().equals("RANKED_SOLO_5x5")) {
                    flexRankColor = setRankColorDependsOnRank(rank.getTier());
                    log.warn(flexRankColor);
                }
            }
        }
        return Summoner.builder().rankSoloColor(soloRankColor).rankFlexColor(flexRankColor).build();
    }



    private String getLatestLoLVersion() {
        return provider.getLatestLoLVersion();
    }

    private SummonerInfo getSummonerInfo(String summonerName) {
        return provider.getSummonerInfo(summonerName);
    }

    private List<Rank> getSummonerRank(final String summonerId) {
        return provider.getLeagueV4Info(summonerId);
    }



    String getChampionById(final int championId, final String latestLoLVersion) {
        if (championId == -1) {
            return "brak";
        }
        String championName = getChampionByKey(championId, allChampionClient.getChampionById(latestLoLVersion).get("data")).get("name").asText();
        return championName != null ? championName.replaceAll("[\\s'.]+", "") : "Brak takiego championka";
    }

    private static JsonNode getChampionByKey(int key, JsonNode champions) {
        for (JsonNode championNode : champions) {
            int championKey = Integer.parseInt(championNode.get("key").asText());

            if (championKey == key) {
                return championNode;
            }
        }
        return null;
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
    //TODO ZAMIENIONE METODY
/*
    private String getProfileIconUrl(String summonerIconId) {
        return ICON_URL + getLatestLoLVersion() + "/img/profileicon/" + summonerIconId + ".png";
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
                        temp.put("championId", m.get("championId"));
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


    JsonNode getSummonerMatchesByNameAndCount(String summonerName, int count) {
        String puuId = getSummonerInfo(summonerName).get("puuid").asText();
        return matchClient.getMatchesByPuuidAndCount(puuId, count, provider.provideKey());
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return matchClient.getInfoAboutMatchById(matchId, provider.provideKey()).get("info");
    }

    JsonNode getInfoAboutAllSummonerInActiveGame(String summonerName) {
        JsonNode summonerInfo = getSummonerInfo(summonerName);
        JsonNode matchInfo = summonerClient.getMatchInfoBySummonerId(summonerInfo.get("id").asText(), provider.provideKey());
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
                bannedChampionsArray.add(champ.get("championId").asText());
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
        JsonNode summonerSpells = allChampionClient.getSummonerSpells(getLatestLoLVersion());

        for (JsonNode n : summonerSpells.get("data")) {
            if (n.get("key").asText().equals(spellId)) {
                return n.get("name").asText();
            }
        }
        return "";
    }

    JsonNode getLast3MatchesBySummonerName(String summonerName) throws InterruptedException {
        JsonNode matchesInfo = getSummonerMatchesByNameAndCount(summonerName, 20);
        ObjectNode summonerInfo = getLeagueInfoFromMatchesList(summonerName);
        getLastRankedMatchesDependsOnCount(summonerInfo, matchesInfo, 3);
        return summonerInfo;
    }

    private JsonNode getLeagueInfo(String summonerId) {
        JsonNode leagueInfo = summonerClient.getLeagueInfoBySummonerId(summonerId, provider.provideKey());
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
        JsonNode exampleMatch = summonerClient.getExampleSummonerNameFromRandomExistingGame(provider.provideKey());
        if (exampleMatch != null && !exampleMatch.get("gameList").isEmpty()) {
            String summonerNameFromExistingGame = exampleMatch.get("gameList").get(0).get("participants").get(0).get("summonerName").asText();
            return summonerNameFromExistingGame != null ? summonerNameFromExistingGame : "Brak listy gier. Spróbuj ponownie za chwilę";
        }
        return "Brak listy gier. Spróbuj ponownie za chwilę";
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
    */
}
