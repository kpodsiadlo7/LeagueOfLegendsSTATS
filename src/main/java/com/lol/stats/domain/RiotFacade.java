package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.MatchInfoMapper;
import com.lol.stats.adapter.mapper.MatchMapper;
import com.lol.stats.adapter.mapper.PreviousMatchInfoMapper;
import com.lol.stats.adapter.mapper.SummonerMapper;
import com.lol.stats.dto.*;
import com.lol.stats.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotFacade {

    private final PreviousMatchInfoMapper previousMatchInfoMapper;
    private final SummonerProvider summonerProvider;
    private final MatchInfoMapper matchInfoMapper;
    private final SummonerMapper summonerMapper;
    private final ColorProvider colorProvider;
    private final MatchMapper matchMapper;

    public SummonerDto getSummonerInfoByName(final String summonerName) {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);

        List<Rank> ranks = getSummonerRank(summonerInfo.getId());
        ChampionDto champion = getMainChampion(summonerInfo.getPuuid());
        String latestLoLVersion = getLatestLoLVersion();

        return summonerMapper.toDto(bakeSummoner(summonerInfo, ranks, champion, latestLoLVersion));
    }

    private SummonerInfo updateSummonerInfo(SummonerInfo summonerInfo) {
        SummonerInfo summonerData = summonerProvider.getSummonerByPuuId(summonerInfo.getPuuid());
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

    private Summoner bakeSummoner(final SummonerInfo summonerInfo, final List<Rank> ranks, final ChampionDto champion, final String latestLoLVersion) {
        Summoner summoner = setRanksForSoloAndFlex(ranks);

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
                .mainChamp(champion != null ? getChampionById(champion.championId(), latestLoLVersion) : "Darius")
                .versionLoL(latestLoLVersion)
                .build();
    }

    private ChampionDto getMainChampion(final String puuid) {
        List<ChampionDto> champions = summonerProvider.getChampionsByPuuId(puuid);
        return getFirstChampionDtoOrElseNull(champions);
    }

    private static ChampionDto getFirstChampionDtoOrElseNull(List<ChampionDto> champions) {
        return champions == null ? null : champions.stream().findFirst().orElse(null);
    }

    private Summoner setRanksForSoloAndFlex(List<Rank> ranks) {
        AtomicReference<String> flexRankColor = new AtomicReference<>("");
        AtomicReference<String> soloRankColor = new AtomicReference<>("");

        if (ranks != null && !ranks.isEmpty()) {
            ranks.forEach(rank -> {
                if ("RANKED_FLEX_SR".equals(rank.getQueueType())) {
                    flexRankColor.set(colorProvider.provideColor(rank.getTier()));
                } else if ("RANKED_SOLO_5x5".equals(rank.getQueueType())) {
                    soloRankColor.set(colorProvider.provideColor(rank.getTier()));
                }
            });
        }
        return Summoner.builder().rankSoloColor(String.valueOf(soloRankColor)).rankFlexColor(String.valueOf(flexRankColor)).build();
    }


    private String getLatestLoLVersion() {
        return summonerProvider.getLatestLoLVersion();
    }

    SummonerInfo getSummonerInfo(String summonerName) {
        SummonerInfo summonerInfo = summonerProvider.getSummonerInfo(summonerName);
        if (summonerInfo == null) return new SummonerInfo();

        // if the tagline is not null, we must collect the remaining data of the summoner using their puuId
        // if the tagline is null it's mean we are not looking for name using '#'
        if (summonerInfo.getTagLine() != null) {
            summonerInfo = updateSummonerInfo(summonerInfo);
        }

        // if name is null it's mean summoner not found
        if (summonerInfo.getName() == null) return new SummonerInfo();

        return summonerInfo;
    }

    private List<Rank> getSummonerRank(final String summonerId) {
        return summonerProvider.getLeagueV4Info(summonerId);
    }


    String getChampionById(final int championId, final String latestLoLVersion) {
        if (championId == -1) return "brak";

        String championName = getChampionByKey(championId, summonerProvider.getAllChampionsDependsOnLoLVersion(latestLoLVersion).get("data")).get("name").asText();
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

    public List<String> getSummonerMatchesByNameAndCount(final String puuId, final int count) {
        return summonerProvider.getMatchesByPuuIdAndCount(puuId, count);
    }

    JsonNode getInfoAboutMatchById(String matchId) {
        return summonerProvider.getInfoAboutMatchById(matchId);
    }

    public MatchInfoDto getInfoAboutAllSummonerInActiveGame(String summonerName) {
        SummonerInfo summonerInfo = getSummonerInfo(summonerName);
        JsonNode matchInfo = summonerProvider.getMatchInfoBySummonerId(summonerInfo.getId());

        MatchInfo allInfoAboutMatch = MatchInfo.builder().summoners(new ArrayList<>()).bannedChampions(new ArrayList<>()).build();

        if (isParticipants(matchInfo)) {
            setInfoAboutSummoners(matchInfo, allInfoAboutMatch, summonerInfo);
        }
        return matchInfoMapper.toDto(allInfoAboutMatch);
    }

    private static boolean isParticipants(JsonNode matchInfo) {
        return !matchInfo.isEmpty() && !matchInfo.get("participants").isEmpty();
    }

    private void setInfoAboutSummoners(JsonNode matchInfo, MatchInfo allInfoAboutMatch, SummonerInfo summonerInfo) {
        for (JsonNode s : matchInfo.get("participants")) {
            initializeSummonerWithSpellIdAndUserTeam(allInfoAboutMatch, summonerInfo, s);
        }

        for (JsonNode champ : matchInfo.get("bannedChampions")) {
            allInfoAboutMatch.getBannedChampions().add(new BannedChampion(champ.get("championId").asText()));
        }
        allInfoAboutMatch.setGameMode(matchInfo.get("gameMode").asText());
    }

    private void initializeSummonerWithSpellIdAndUserTeam(MatchInfo allInfoAboutMatch, SummonerInfo summonerInfo, JsonNode s) {
        MatchSummoner matchSummoner = setMatchSummoner(s);
        matchSummoner.setSpellName1(getSpellNameBySpellId(s.get("spell1Id").asText()));
        matchSummoner.setSpellName2(getSpellNameBySpellId(s.get("spell2Id").asText()));

        allInfoAboutMatch.getSummoners().add(matchSummoner);

        if (isMatchInfoHasSummonerId(summonerInfo, s))
            allInfoAboutMatch.setUserTeam(s.get("teamId").asText());
    }

    private static boolean isMatchInfoHasSummonerId(SummonerInfo summonerInfo, JsonNode s) {
        return s.get("summonerId").asText().equals(summonerInfo.getId());
    }

    private String checkIfNameIsNotEmpty(final String name, final String puuId) {
        if (!name.isEmpty()) return name;
        log.warn("empty nick");

        return getNameOrGameName(name,puuId);
    }

    private String getNameOrGameName(String name, String puuId) {
        SummonerInfo summonerInfo = getSummonerNameFromAccountData(puuId);
        return summonerInfo == null ? name : summonerInfo.getGameName();
    }

    private SummonerInfo getSummonerNameFromAccountData(String puuId) {
        return summonerProvider.getSummonerFromAccountData(puuId);
    }

    private Match setRankedSoloRank(List<Rank> ranks) {
        AtomicReference<String> rank = new AtomicReference<>("BRAK RANGI");
        AtomicReference<String> rankColor = new AtomicReference<>("#363949");
        if (ranks != null && !ranks.isEmpty()) {
            ranks.forEach(r -> {
                if (r.getQueueType().equals("RANKED_SOLO_5x5")) {
                    rank.set(r.getTier());
                    rankColor.set(colorProvider.provideColor(rank.get()));
                }
            });
        }
        return Match.builder().rank(String.valueOf(rank)).rankColor(String.valueOf(rankColor)).build();
    }

    private String getSpellNameBySpellId(String spellId) {
        JsonNode summonerSpells = summonerProvider.getSummonerSpells(getLatestLoLVersion());

        return getSpellNameFromSummonerSpellsOrReturnEmptyString(spellId, summonerSpells);
    }

    private static String getSpellNameFromSummonerSpellsOrReturnEmptyString(String spellId, JsonNode summonerSpells) {
        for (JsonNode n : summonerSpells.get("data")) {
            if (n.get("key").asText().equals(spellId)) {
                return n.get("name").asText();
            }
        }
        return "";
    }

    private LeagueInfoDto getLeagueInfo(String summonerId) {
        List<LeagueInfoDto> leagueInfoList = summonerProvider.getLeagueInfoListBySummonerId(summonerId);
        return getLeagueInfoDtoOrReturnNull(leagueInfoList);
    }

    private static LeagueInfoDto getLeagueInfoDtoOrReturnNull(List<LeagueInfoDto> leagueInfoList) {
        if (leagueInfoList != null && !leagueInfoList.isEmpty()) {
            return getRankedSolo5x5OrReturnNull(leagueInfoList);
        }
        return null;
    }

    private static LeagueInfoDto getRankedSolo5x5OrReturnNull(List<LeagueInfoDto> leagueInfoList) {
        return leagueInfoList.stream()
                .filter(league -> league.queueType().equals("RANKED_SOLO_5x5"))
                .findFirst()
                .orElse(null);
    }

    public String getRandomSummonerNameFromExistingGame() {
        JsonNode exampleMatch = summonerProvider.getExampleSummonerNameFromExistingGame();
        return ifExampleMatchHasGameListGetNameAndPuuIdAndCheckName(exampleMatch);
    }

    private String ifExampleMatchHasGameListGetNameAndPuuIdAndCheckName(JsonNode exampleMatch) {
        if (isGameList(exampleMatch)) {
            String name = exampleMatch.get("gameList").get(0).get("participants").get(0).get("summonerName").asText();
            String puuId = exampleMatch.get("gameList").get(0).get("participants").get(0).get("puuid").asText();

            String summonerNameFromExistingGame = checkIfNameIsNotEmpty(name, puuId);
            return summonerNameFromExistingGame != null ? summonerNameFromExistingGame : "Brak listy gier. Spróbuj ponownie za chwilę";
        }
        return "Brak listy gier. Spróbuj ponownie za chwilę";
    }

    private static boolean isGameList(JsonNode exampleMatch) {
        return exampleMatch != null && !exampleMatch.get("gameList").isEmpty();
    }


    public MatchDto getLastMatchesByPuuIdAndCounts(final String puuId, final int matchesListCount, final int rankedCount) throws InterruptedException {
        List<String> matchesIdList = getSummonerMatchesByNameAndCount(puuId, matchesListCount);
        Match match = getLeagueInfoFromMatchesList(puuId);

        return matchMapper.toDto(getLastRankedMatchesDependsOnCount(match, matchesIdList, rankedCount));
    }

    private Match getLastRankedMatchesDependsOnCount(Match leagueInfo, List<String> matchesIdList, int count) throws InterruptedException {
        int matches = 0;
        int questions = 0;
        int wins = 0;
        int losses = 0;
        for (var singleMatch : matchesIdList) {
            JsonNode matchJN = getInfoAboutMatchById(singleMatch);
            if (isGameModeIsClassic(matchJN)) {
                for (JsonNode m : matchJN.get("info").get("participants")) {
                    ChampMatch champMatch = new ChampMatch();
                    if (m.get("puuid").asText().equals(leagueInfo.getPuuid())) {
                        setChampMatch(singleMatch, m, champMatch);
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
            int MAX_ATTEMPT = 15;
            if (questions >= MAX_ATTEMPT) {
                sleep(1000);
                questions = 0;
            }
        }
        leagueInfo.setWins(wins);
        leagueInfo.setLosses(losses);
        return leagueInfo;
    }

    private static boolean isGameModeIsClassic(JsonNode matchJN) {
        return matchJN.get("info").get("gameMode").asText().equals("CLASSIC");
    }

    private void setChampMatch(String singleMatch, JsonNode m, ChampMatch champMatch) {
        champMatch.setMatchId(singleMatch);
        champMatch.setMatchChampName(m.get("championName").asText());
        champMatch.setChampionId(m.get("championId").asInt());
        champMatch.setAssists(m.get("assists").asInt());
        champMatch.setKda(m.get("challenges").get("kda").asInt());
        champMatch.setDeaths(m.get("deaths").asInt());
        champMatch.setKills(m.get("kills").asInt());
        champMatch.setLane(getLane(m));
        champMatch.setDealtDamage(m.get("totalDamageDealtToChampions").asInt());
        champMatch.setTeamId(m.get("teamId").asInt());
        champMatch.setWin(m.get("win").asBoolean());
    }

    private static String getLane(JsonNode m) {
        String lane = m.get("lane").asText();
        String teamPosition = m.get("teamPosition").asText();
        String individualPosition = m.get("individualPosition").asText();
        String isLane =
                !teamPosition.equals("UTILITY") && !teamPosition.equals("NONE") ? teamPosition :
                        !individualPosition.equals("UTILITY") && !individualPosition.equals("NONE") ? individualPosition :
                                !lane.equals("UTILITY") && !lane.equals("NONE") ? lane : "UTILITY";

        isLane = m.get("role").asText().equals("SUPPORT") && isLane.equals("BOTTOM") ? "SUPPORT" : isLane;
        return isLane;
    }

    private Match getLeagueInfoFromMatchesList(final String puuId) {
        SummonerInfo summonerInfo = summonerProvider.getSummonerByPuuId(puuId);

        String summonerId = summonerInfo.getId();
        LeagueInfoDto leagueInfo = getLeagueInfo(summonerId);
        Match match = setRankedSoloRank(getSummonerRank(summonerId));

        return bakeMatch(summonerInfo, leagueInfo, match);
    }

    private Match bakeMatch(final SummonerInfo summonerInfo, final LeagueInfoDto leagueInfo, final Match match) {
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
        List<PreviousMatchSummoner> summoners = new ArrayList<>();
        PreviousMatchInfo previousMatchInfo = new PreviousMatchInfo();

        return getPreviousMatchInfoDto(matchId, summoners, previousMatchInfo);
    }

    private PreviousMatchInfoDto getPreviousMatchInfoDto(String matchId, List<PreviousMatchSummoner> summoners, PreviousMatchInfo previousMatchInfo) {
        JsonNode matchInfo = summonerProvider.getInfoAboutMatchById(matchId);
        if (matchInfo == null) return null;

        JsonNode info = matchInfo.get("info");
        if (info == null) return null;

        return getPreviousMatchInfoDto(summoners, previousMatchInfo, info, matchInfo);
    }

    private PreviousMatchInfoDto getPreviousMatchInfoDto(List<PreviousMatchSummoner> summoners, PreviousMatchInfo previousMatchInfo, JsonNode info, JsonNode matchInfo) {
        ifInfoHasParticipantsSetPreviousMatchSummonerAndCheckTime(summoners, previousMatchInfo, info);

        // include banned champions list
        setObjectives(info, previousMatchInfo);

        previousMatchInfo.setSummoners(summoners);
        previousMatchInfo.setMatchId(matchInfo.get("metadata").get("matchId").asText());

        return previousMatchInfoMapper.toDto(previousMatchInfo);
    }

    private void ifInfoHasParticipantsSetPreviousMatchSummonerAndCheckTime(List<PreviousMatchSummoner> summoners, PreviousMatchInfo previousMatchInfo, JsonNode info) {
        for (var summoner : info.get("participants")) {
            summoners.add(setPreviousMatchSummoner(summoner));
            ifPreviousMatchTimeIsLessThan1ThenSetTimeInSecond(previousMatchInfo, summoner);
        }
    }

    private static void ifPreviousMatchTimeIsLessThan1ThenSetTimeInSecond(PreviousMatchInfo previousMatchInfo, JsonNode summoner) {
        if (previousMatchInfo.getTimeInSeconds() < 1) {
            previousMatchInfo.setTimeInSeconds(summoner.get("timePlayed").asInt());
        }
    }

    private PreviousMatchSummoner setPreviousMatchSummoner(JsonNode summoner) {
        SummonerInfo summonerInfo = getSummonerInfoFromPuuIdOrBuildBOTSummonerInfo(summoner);
        List<Rank> ranks = new ArrayList<>();
        int kda = getKdaOrZERO(summoner);
        if (!summonerInfo.getPuuid().equals("BOT"))
            ranks = getSummonerRank(summoner.get("summonerId").asText());

        Match match = setRankedSoloRank(ranks);
        int champIconId = summoner.get("championId").asInt();
        return buildPreviousMatchForCurrentSummoner(summoner, summonerInfo, match, champIconId, kda);
    }

    private static int getKdaOrZERO(JsonNode summoner) {
        return summoner.get("challenges") != null ? summoner.get("challenges").get("kda").asInt() : 0;
    }

    private SummonerInfo getSummonerInfoFromPuuIdOrBuildBOTSummonerInfo(JsonNode summoner) {
        return !summoner.get("puuid").asText().equals("BOT") ?
                summonerProvider.getSummonerByPuuId(summoner.get("puuid").asText()) :
                SummonerInfo.builder().name("BOT").puuid("BOT").build();
    }

    private PreviousMatchSummoner buildPreviousMatchForCurrentSummoner(JsonNode summoner, SummonerInfo summonerInfo, Match match, int champIconId, int kda) {
        return PreviousMatchSummoner.builder()
                .lane(getLane(summoner))
                .puuId(summonerInfo.getPuuid())
                .summonerName(checkIfNameIsNotEmpty(summonerInfo.getName(), summonerInfo.getPuuid()))
                .rank(match.getRank())
                .rankColor(match.getRankColor())
                .matchChampName(getChampionById(champIconId, getLatestLoLVersion()))
                .championId(champIconId)
                .assists(summoner.get("assists").asInt())
                .kda(kda)
                .deaths(summoner.get("deaths").asInt())
                .kills(summoner.get("kills").asInt())
                .dealtDamage(summoner.get("totalDamageDealtToChampions").asInt())
                .win(summoner.get("win").asBoolean())
                .teamId(summoner.get("teamId").asInt()).build();
    }

    private void setObjectives(JsonNode info, PreviousMatchInfo previousMatchInfo) {
        for (var objectives : info.get("teams")) {
            addObjectivesToPreviousMatchInfoIfPresent(previousMatchInfo, objectives);
            setBannedList(objectives, previousMatchInfo);
        }
    }

    private static void addObjectivesToPreviousMatchInfoIfPresent(PreviousMatchInfo previousMatchInfo, JsonNode objectives) {
        if (objectives.has("objectives")) {
            var objective = objectives.get("objectives");
            String baronKills = objective.get("baron").get("kills").asText();
            String championKills = objective.get("champion").get("kills").asText();
            String dragonKills = objective.get("dragon").get("kills").asText();
            int teamId = objectives.get("teamId").asInt();

            previousMatchInfo.getTeamObjective().add(new TeamObjectiveDto(championKills, baronKills, dragonKills, teamId));
        }
    }

    private void setBannedList(JsonNode objectives, PreviousMatchInfo previousMatchInfo) {
        if (objectives.has("bans")) {
            fillBannedChampionsListIfPresent(objectives, previousMatchInfo);
        }
    }

    private static void fillBannedChampionsListIfPresent(JsonNode objectives, PreviousMatchInfo previousMatchInfo) {
        var bans = objectives.get("bans");
        for (var bannedChamp : bans) {
            previousMatchInfo.getBannedChampions()
                    .add(new BannedChampion(bannedChamp.get("championId").asText()));
        }
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
