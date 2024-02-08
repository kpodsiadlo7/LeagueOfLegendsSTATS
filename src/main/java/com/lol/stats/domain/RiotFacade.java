package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.MatchInfoMapper;
import com.lol.stats.adapter.mapper.MatchMapper;
import com.lol.stats.adapter.mapper.PreviousMatchInfoMapper;
import com.lol.stats.adapter.mapper.SummonerMapper;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.PreviousMatchInfoDto;
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

    private final PreviousMatchInfoMapper previousMatchInfoMapper;
    private final MatchInfoMapper matchInfoMapper;
    private final SummonerMapper summonerMapper;
    private final MatchMapper matchMapper;
    private final Provider provider;

    public SummonerDto getSummonerInfoByName(final String summonerName) {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);

        List<Rank> ranks = getSummonerRank(summonerInfo.getId());
        Champion champion = getMainChampion(summonerInfo.getPuuid());
        String latestLoLVersion = getLatestLoLVersion();

        return summonerMapper.toDto(bakeSummoner(summonerInfo, ranks, champion, latestLoLVersion));
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
        if (summonerInfo.getTagLine() != null) {
            summonerInfo = updateSummonerInfo(summonerInfo);
        }

        // if name is null it's mean summoner is not found
        if (summonerInfo.getName() == null) return new SummonerInfo();

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
            setInfoAboutSummoners(matchInfo, allInfoAboutMatch, summonerInfo);
        }
        return matchInfoMapper.toDto(allInfoAboutMatch);
    }

    private void setInfoAboutSummoners(JsonNode matchInfo, MatchInfo allInfoAboutMatch, SummonerInfo summonerInfo) {
        for (JsonNode s : matchInfo.get("participants")) {
            MatchSummoner matchSummoner = setMatchSummoner(s);
            matchSummoner.setSpellName1(getSpellNameBySpellId(s.get("spell1Id").asText()));
            matchSummoner.setSpellName2(getSpellNameBySpellId(s.get("spell2Id").asText()));

            allInfoAboutMatch.getSummoners().add(matchSummoner);

            if (s.get("summonerId").asText().equals(summonerInfo.getId()))
                allInfoAboutMatch.setUserTeam(s.get("teamId").asText());
        }

        for (JsonNode champ : matchInfo.get("bannedChampions")) {
            allInfoAboutMatch.getBannedChampions().add(new BannedChampion(champ.get("championId").asText()));
        }
        allInfoAboutMatch.setGameMode(matchInfo.get("gameMode").asText());
    }

    private String checkIfNameIsNotEmpty(final String name, final String puuId) {
        if (!name.isEmpty()) return name;
        log.warn("empty nick");
        SummonerInfo summonerInfo = provider.getSummonerFromAccountData(puuId);
        return summonerInfo == null ? name : summonerInfo.getGameName();
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
            String name = exampleMatch.get("gameList").get(0).get("participants").get(0).get("summonerName").asText();
            String puuId = exampleMatch.get("gameList").get(0).get("participants").get(0).get("puuid").asText();
            
            String summonerNameFromExistingGame = checkIfNameIsNotEmpty(name,puuId);
            return summonerNameFromExistingGame != null ? summonerNameFromExistingGame : "Brak listy gier. Spróbuj ponownie za chwilę";
        }
        return "Brak listy gier. Spróbuj ponownie za chwilę";
    }


    public MatchDto getLastMatchesByPuuIdAndCounts(final String puuId, final int matchesListCount, final int rankedCount) throws InterruptedException {
        List<String> matchesIdList = getSummonerMatchesByNameAndCount(puuId, matchesListCount);
        Match leagueInfo = getLeagueInfoFromMatchesList(puuId);

        return matchMapper.toDto(getLastRankedMatchesDependsOnCount(leagueInfo, matchesIdList, rankedCount));
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
                        champMatch = setChampMatch(singleMatch, m, champMatch);
                        switch (m.get("win").asText()) {
                            case "true" -> {
                                champMatch.setWin(true);
                                champMatch.setWinColor("green");
                                wins++;
                            }

                            case "false" -> {
                                champMatch.setWin(false);
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

    private ChampMatch setChampMatch(String singleMatch, JsonNode m, ChampMatch champMatch) {
        String lane = m.get("lane").asText();
        String teamPosition = m.get("teamPosition").asText();
        String individualPosition = m.get("individualPosition").asText();
        String isLane =
                !lane.equals("UTILITY") && !lane.equals("NONE") ? lane :
                        (!teamPosition.equals("UTILITY") && !teamPosition.equals("NONE")) ? teamPosition :
                                (!individualPosition.equals("UTILITY") && !individualPosition.equals("NONE")) ? individualPosition : "UNKNOWN";

        champMatch.setMatchId(singleMatch);
        champMatch.setMatchChampName(m.get("championName").asText());
        champMatch.setChampionId(m.get("championId").asInt());
        champMatch.setAssists(m.get("assists").asInt());
        champMatch.setKda(m.get("challenges").get("kda").asInt());
        champMatch.setDeaths(m.get("deaths").asInt());
        champMatch.setKills(m.get("kills").asInt());
        champMatch.setLane(isLane);
        champMatch.setDealtDamage(m.get("totalDamageDealtToChampions").asInt());
        champMatch.setTeamId(m.get("teamId").asInt());
        champMatch.setWin(m.get("win").asBoolean());
        return champMatch;
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
                .summonerLevel(summonerInfo.getSummonerLevel())
                .leagueInfo(leagueInfo)
                .rank(match.getRank())
                .rankColor(match.getRankColor())
                .matches(new ArrayList<>()).build();
    }

    public PreviousMatchInfoDto getPreviousMatchByMatchId(final String matchId) {
        List<ChampMatch> matchList = new ArrayList<>();
        PreviousMatchInfo previousMatchInfo = new PreviousMatchInfo();
        MatchInfo allInfoAboutMatch = MatchInfo.builder().summoners(new ArrayList<>()).bannedChampions(new ArrayList<>()).build();

        JsonNode matchInfo = provider.getInfoAboutMatchById(matchId);
        if (matchInfo == null) return null;

        JsonNode info = matchInfo.get("info");
        if (info == null) return null;

        for (var summoner : info.get("participants")) {
            ChampMatch champMatch = new ChampMatch();
            matchList.add(setChampMatch(matchId, summoner, champMatch));
            SummonerInfo summonerInfo = provider.getSummonerByPuuId(summoner.get("puuid").asText());
            MatchSummoner matchSummoner = setMatchSummoner(summoner);
            matchSummoner.setSpellName1(getSpellNameBySpellId(summoner.get("summoner1Id").asText()));
            matchSummoner.setSpellName2(getSpellNameBySpellId(summoner.get("summoner2Id").asText()));
            allInfoAboutMatch.getSummoners().add(matchSummoner);

            if (summoner.get("summonerId").asText().equals(summonerInfo.getId()))
                allInfoAboutMatch.setUserTeam(summoner.get("teamId").asText());
        }

        for (var objectives : info.get("teams")) {
            if (objectives.has("objectives")) {
                TeamObjective teamObjective = new TeamObjective();
                var objective = objectives.get("objectives");

                if (objective.has("baron"))
                    teamObjective.setBaronKills(objective.get("baron").get("kills").asText());
                if (objective.has("champion"))
                    teamObjective.setChampionKills(objective.get("champion").get("kills").asText());
                if (objective.has("dragon"))
                    teamObjective.setDragonKills(objective.get("dragon").get("kills").asText());
                if (objectives.has("teamId")) teamObjective.setTeamId(objectives.get("teamId").asInt());

                previousMatchInfo.getTeamObjective().add(teamObjective);
            }
            if (objectives.has("bans")) {
                var bans = objectives.get("bans");
                for (var bannedChamp : bans) {
                    allInfoAboutMatch.getBannedChampions()
                            .add(new BannedChampion(getChampionById(bannedChamp.get("championId").asInt(), getLatestLoLVersion())));
                }
            }
        }
        allInfoAboutMatch.setGameMode(info.get("gameMode").asText());
        previousMatchInfo.setMatchInfo(allInfoAboutMatch);
        previousMatchInfo.setMatchList(matchList);
        return previousMatchInfoMapper.toDto(previousMatchInfo);
    }

    private MatchSummoner setMatchSummoner(final JsonNode s) {
        List<Rank> ranks = getSummonerRank(s.get("summonerId").asText());
        MatchSummoner matchSummoner = new MatchSummoner();
        Match match = setRankedSoloRank(ranks);

        String puuId = s.get("puuid").asText();
        String name = s.get("summonerName").asText();
        name = checkIfNameIsNotEmpty(name, puuId);

        matchSummoner.setPuuid(puuId);
        matchSummoner.setTeamId(s.get("teamId").asInt());
        matchSummoner.setChampionId(s.get("championId").asInt());
        matchSummoner.setSummonerName(name);
        matchSummoner.setSummonerId(s.get("summonerId").asText());
        matchSummoner.setRank(match.getRank());
        matchSummoner.setRankColor(match.getRankColor());
        matchSummoner.setChampName(getChampionById(s.get("championId").asInt(), getLatestLoLVersion()));
        return matchSummoner;
    }
}
