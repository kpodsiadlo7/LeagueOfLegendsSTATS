package com.lol.stats.adapter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.ChampionMapper;
import com.lol.stats.adapter.mapper.LeagueMapper;
import com.lol.stats.adapter.mapper.RankMapper;
import com.lol.stats.adapter.mapper.SummonerMapper;
import com.lol.stats.domain.*;
import com.lol.stats.domain.client.DDragonClient;
import com.lol.stats.domain.client.EuropeRiotClient;
import com.lol.stats.domain.client.EUN1RiotClient;
import com.lol.stats.model.Champion;
import com.lol.stats.model.LeagueInfo;
import com.lol.stats.model.Rank;
import com.lol.stats.model.SummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderImpl implements Provider {

    private final EUN1RiotClient eun1RiotClient;
    private final SummonerMapper summonerMapper;
    private final ChampionMapper championMapper;
    private final RankMapper rankMapper;
    private final LeagueMapper leagueMapper;
    private final EuropeRiotClient europeRiotClient;
    private final DDragonClient dDragonClient;

    @Override
    public String provideKey() {
        try {
            String apiKeyFilePath = "x:/key.txt";
            Path path = Paths.get(apiKeyFilePath);
            byte[] apiKeyBytes = Files.readAllBytes(path);
            return new String(apiKeyBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "INVALID API_KEY";
    }

    @Override
    public SummonerInfo getSummonerInfo(final String summonerName) {
        return summonerMapper.fromSummonerInfoDto(eun1RiotClient.getSummonerByName(summonerName, provideKey()));
    }

    @Override
    public String getLatestLoLVersion() {
        return dDragonClient.getLolVersions()[0];
    }

    @Override
    public List<Rank> getLeagueV4Info(final String summonerId) {
        return rankMapper.mapToRankListFromRankDtoList(eun1RiotClient.getLeagueV4(summonerId, provideKey()));
    }

    @Override
    public List<Champion> getChampionsByPuuId(final String puuid) {
        return championMapper.mapToChampionListFromChampionDtoList(eun1RiotClient.getChampions(puuid, provideKey()));
    }

    @Override
    public List<String> getMatchesByPuuIdAndCount(final String puuid, final int count) {
        return europeRiotClient.getMatchesByPuuIdAndCount(puuid, count, provideKey());
    }

    @Override
    public List<LeagueInfo> getLeagueInfoListBySummonerId(String summonerId) {
        return leagueMapper.mapToLeagueInfoListFromLeagueInfoDtoList(eun1RiotClient.getLeagueInfoBySummonerId(summonerId, provideKey()));
    }

    @Override
    public JsonNode getExampleSummonerNameFromExistingGame() {
        return eun1RiotClient.getExampleSummonerNameFromRandomExistingGame(provideKey());
    }

    @Override
    public JsonNode getAllChampionsDependsOnLoLVersion(String latestLoLVersion) {
        return dDragonClient.getChampionById(latestLoLVersion);
    }

    @Override
    public JsonNode getInfoAboutMatchById(String matchId) {
        return europeRiotClient.getInfoAboutMatchById(matchId, provideKey());
    }

    @Override
    public JsonNode getMatchInfoBySummonerId(String id) {
        return eun1RiotClient.getMatchInfoBySummonerId(id, provideKey());
    }

    @Override
    public JsonNode getSummonerSpells(String latestLoLVersion) {
        return dDragonClient.getSummonerSpells(latestLoLVersion);
    }
}
