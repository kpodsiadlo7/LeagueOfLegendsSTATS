package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.MatchMapper;
import com.lol.stats.adapter.mapper.SummonerMapper;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.SummonerDto;
import com.lol.stats.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotFacade {

    private final SummonerMapper summonerMapper;
    private final MatchMapper matchMapper;
    private final Provider provider;

    public SummonerDto getSummonerInfoByName(final String summonerName) {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);

        List<Rank> ranks = getSummonerRank(summonerInfo.getId());
        Champion champion = getMainChampion(summonerInfo.getPuuid());
        String latestLoLVersion = getLatestLoLVersion();

        return summonerMapper.mapToSummonerDtoFromSummoner(bakeSummoner(summonerInfo, ranks, champion, latestLoLVersion));
    }

    private SummonerInfo updateSummonerInfo(SummonerInfo summonerInfo) {
        SummonerInfo summonerData = provider.getSummonerByPuuId(summonerInfo.getPuuid());
        return SummonerInfo.builder()
                .id(summonerData.getId())
                .accountId(summonerData.getAccountId())
                .puuid(summonerData.getPuuid())
                .tagLine(summonerInfo.getTagLine())
                .name(summonerInfo.getGameName())
                .gameName(summonerInfo.getGameName())
                .profileIconId(summonerData.getProfileIconId())
                .summonerLevel(summonerData.getSummonerLevel()).build();
    }

    private Summoner bakeSummoner(final SummonerInfo summonerInfo, final List<Rank> ranks, final Champion champion, final String latestLoLVersion) {
        Summoner summoner = setRanksForSoloAndFlex(ranks);
        String mainChampName = getChampionById(champion.getChampionId(), latestLoLVersion);

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
        if (champions != null && !champions.isEmpty()) {
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
                    flexRankColor = setRankColorDependsOnTier(rank.getTier());

                } else if (rank.getQueueType().equals("RANKED_SOLO_5x5")) {
                    soloRankColor = setRankColorDependsOnTier(rank.getTier());
                }
            }
        }
        return Summoner.builder().rankSoloColor(soloRankColor).rankFlexColor(flexRankColor).build();
    }


    private String getLatestLoLVersion() {
        return provider.getLatestLoLVersion();
    }

    private SummonerInfo getSummonerInfo(String summonerName) {
        SummonerInfo summonerInfo = provider.getSummonerInfo(summonerName);
        if (summonerInfo == null) return new SummonerInfo();

        // if the tagline is not null, we must collect the remaining data of the summoner using their puuId
        // if the tagline is null it's mean we are not looking name using '#'
        if (summonerInfo.getTagLine() != null){
            summonerInfo = updateSummonerInfo(summonerInfo);
        }

        // if name is null it's mean summoner is not found
        if(summonerInfo.getName() == null) return new SummonerInfo();

        return summonerInfo;
    }

    private List<Rank> getSummonerRank(final String summonerId) {
        return provider.getLeagueV4Info(summonerId);
    }


    String getChampionById(final int championId, final String latestLoLVersion) {
        if (championId == -1) {
            return "brak";
        }
        String championName = getChampionByKey(championId, provider.getAllChampionsDependsOnLoLVersion(latestLoLVersion).get("data")).get("name").asText();
        return championName != null ? championName.replaceAll("[\\s'.]+", "") : "Brak takiego championka";
    }

    private static JsonNode getChampionByKey(int key, JsonNode champions) {
        if (champions != null && !champions.isEmpty()) {
            for (JsonNode championNode : champions) {
                int championKey = Integer.parseInt(championNode.get("key").asText());
                if (championKey == key) {
                    return championNode;
                }
            }
        }
        return null;
    }

    private String setRankColorDependsOnTier(final String rank) {
        String lowerCaseRank = rank.toLowerCase();
        return switch (lowerCaseRank) {
            case "gold" -> "#FFD700";
            case "silver" -> "#C0C0C0";
            case "platinum" -> "#A9A9A9";
            case "emerald" -> "#2ecc71";
            case "diamond" -> "#00CED1";
            case "bronze" -> "#964B00";
            case "grandmaster" -> "#000080";
            case "master" -> "#800080";
            default -> "#363949";
        };
    }

    public List<String> getSummonerMatchesByNameAndCount(final String puuId, final int count) {
        return provider.getMatchesByPuuIdAndCount(puuId, count);
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return provider.getInfoAboutMatchById(matchId);
    }

    public MatchInfoDto getInfoAboutAllSummonerInActiveGame(String summonerName) {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);
        JsonNode matchInfo = provider.getMatchInfoBySummonerId(summonerInfo.getId());

        MatchInfo allInfoAboutMatch = MatchInfo.builder().summoners(new ArrayList<>()).bannedChampions(new ArrayList<>()).build();

        if (!matchInfo.isEmpty() && !matchInfo.get("participants").isEmpty()) {
            String userTeam = null;
            for (JsonNode s : matchInfo.get("participants")) {
                List<Rank> ranks = getSummonerRank(s.get("summonerId").asText());
                MatchSummoner matchSummoner = new MatchSummoner();
                Match match = setRankedSoloRank(ranks);

                matchSummoner.setPuuid(s.get("puuid").asText());
                matchSummoner.setTeamId(s.get("teamId").asInt());
                matchSummoner.setChampionId(s.get("championId").asInt());
                matchSummoner.setSummonerName(s.get("summonerName").asText());
                matchSummoner.setSummonerId(s.get("summonerId").asText());
                matchSummoner.setRank(match.getRank());
                matchSummoner.setRankColor(match.getRankColor());
                matchSummoner.setChampName(getChampionById(s.get("championId").asInt(), getLatestLoLVersion()));
                matchSummoner.setSpellName1(getSpellNameBySpellId(s.get("spell1Id").asText()));
                matchSummoner.setSpellName2(getSpellNameBySpellId(s.get("spell2Id").asText()));

                allInfoAboutMatch.getSummoners().add(matchSummoner);

                if (s.get("summonerId").asText().equals(summonerInfo.getId()))
                    userTeam = s.get("teamId").asText();
            }

            for (JsonNode champ : matchInfo.get("bannedChampions")) {
                allInfoAboutMatch.getBannedChampions().add(new BannedChampion(champ.get("championId").asText()));
            }
            allInfoAboutMatch.setUserTeam(userTeam);
            allInfoAboutMatch.setGameMode(matchInfo.get("gameMode").asText());
        }
        return matchMapper.mapToMatchInfoDtoFromMatchInfo(allInfoAboutMatch);
    }


    private Match setRankedSoloRank(List<Rank> ranks) {
        String rank = "BRAK RANGI";
        String rankColor = "#363949";
        if (ranks != null && !ranks.isEmpty()) {
            for (Rank r : ranks) {
                if (r.getQueueType().equals("RANKED_SOLO_5x5")) {
                    rank = r.getTier();
                    rankColor = setRankColorDependsOnTier(rank);
                    return Match.builder().rank(rank).rankColor(rankColor).build();
                }
            }
        }
        return Match.builder().rank(rank).rankColor(rankColor).build();
    }

    private String getSpellNameBySpellId(String spellId) {
        JsonNode summonerSpells = provider.getSummonerSpells(getLatestLoLVersion());

        for (JsonNode n : summonerSpells.get("data")) {
            if (n.get("key").asText().equals(spellId)) {
                return n.get("name").asText();
            }
        }
        return "";
    }

    private LeagueInfo getLeagueInfo(String summonerId) {
        List<LeagueInfo> leagueInfoList = provider.getLeagueInfoListBySummonerId(summonerId);

        if (leagueInfoList != null && !leagueInfoList.isEmpty()) {
            for (LeagueInfo league : leagueInfoList) {
                if (league.getQueueType().equals("RANKED_SOLO_5x5")) {
                    return league;
                }
            }
        }
        return new LeagueInfo();
    }

    public String getRandomSummonerNameFromExistingGame() {
        JsonNode exampleMatch = provider.getExampleSummonerNameFromExistingGame();
        if (exampleMatch != null && !exampleMatch.get("gameList").isEmpty()) {
            String summonerNameFromExistingGame = exampleMatch.get("gameList").get(0).get("participants").get(0).get("summonerName").asText();
            return summonerNameFromExistingGame != null ? summonerNameFromExistingGame : "Brak listy gier. Spróbuj ponownie za chwilę";
        }
        return "Brak listy gier. Spróbuj ponownie za chwilę";
    }


    public MatchDto getLastMatchesByPuuIdAndCounts(final String summonerName, final int matchesListCount, final int rankedCount) throws InterruptedException {
        List<String> matchesIdList = getSummonerMatchesByNameAndCount(summonerName, matchesListCount);
        Match leagueInfo = getLeagueInfoFromMatchesList(summonerName);

        return matchMapper.mapToMatchDtoFromMatch(getLastRankedMatchesDependsOnCount(leagueInfo, matchesIdList, rankedCount));
    }

    private Match getLastRankedMatchesDependsOnCount(Match leagueInfo, List<String> matchesIdList, int count) throws InterruptedException {
        int matches = 0;
        int questions = 0;
        int wins = 0;
        int losses = 0;
        for (var singleMatch : matchesIdList) {
            JsonNode matchJN = getInfoAboutMatchById(singleMatch);
            if (matchJN.get("info").get("gameMode").asText().equals("CLASSIC")) {
                for (JsonNode m : matchJN.get("info").get("participants")) {
                    ChampMatch champMatch = new ChampMatch();
                    if (m.get("puuid").asText().equals(leagueInfo.getPuuid())) {
                        champMatch.setMatchChampName(m.get("championName").asText());
                        champMatch.setChampionId(m.get("championId").asInt());
                        champMatch.setAssists(m.get("assists").asInt());
                        champMatch.setKda(m.get("challenges").get("kda").asInt());
                        champMatch.setDeaths(m.get("deaths").asInt());
                        champMatch.setKills(m.get("kills").asInt());
                        champMatch.setLane(m.get("teamPosition").asText());
                        champMatch.setDealtDamage(m.get("totalDamageDealtToChampions").asInt());
                        switch (m.get("win").asText()) {
                            case "true" -> {
                                champMatch.setWin("WYGRANA");
                                champMatch.setWinColor("green");
                                wins++;
                            }

                            case "false" -> {
                                champMatch.setWin("PRZEGRANA");
                                champMatch.setWinColor("red");
                                losses++;
                            }
                        }
                        leagueInfo.getMatches().add(champMatch);
                        matches++;
                        if (matches >= count) {
                            leagueInfo.setWins(wins);
                            leagueInfo.setLosses(losses);
                            return leagueInfo;
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
        leagueInfo.setWins(wins);
        leagueInfo.setLosses(losses);
        return leagueInfo;
    }

    private Match getLeagueInfoFromMatchesList(final String puuId) {
        SummonerInfo summonerInfo = provider.getSummonerByPuuId(puuId);

        String summonerId = summonerInfo.getId();
        LeagueInfo leagueInfo = getLeagueInfo(summonerId);
        Match match = setRankedSoloRank(getSummonerRank(summonerId));

        return bakeMatch(summonerInfo, leagueInfo, match);
    }

    private Match bakeMatch(final SummonerInfo summonerInfo, final LeagueInfo leagueInfo, final Match match) {
        return Match.builder()
                .id(summonerInfo.getId())
                .accountId(summonerInfo.getAccountId())
                .puuid(summonerInfo.getPuuid())
                .name(summonerInfo.getName())
                .profileIconId(summonerInfo.getProfileIconId())
                .summonerLevel(summonerInfo.getSummonerLevel())
                .leagueInfo(leagueInfo)
                .rank(match.getRank())
                .rankColor(match.getRankColor())
                .matches(new ArrayList<>()).build();
    }
}
