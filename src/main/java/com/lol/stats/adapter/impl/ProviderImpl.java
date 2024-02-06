package com.lol.stats.adapter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.*;
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
    private final SummonerInfoMapper summonerInfoMapper;
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
        if(summonerName == null || summonerName.isEmpty()) return new SummonerInfo();

        // if we are looking for by name with #hash, we need to use europeRiotClient
        String[] nameAndHash = summonerName.split("#");
        if(nameAndHash.length == 2){
            // nameAndHash[0] <- summonerName, nameAndHash[1] <- summonerHash
            return summonerInfoMapper.fromSummonerInfoDto(
                    europeRiotClient.getSummonerByNameAndHash(nameAndHash[0],nameAndHash[1],provideKey()));
        }
        // we are looking for by name without #hash
        return summonerInfoMapper.fromSummonerInfoDto(eun1RiotClient.getSummonerByName(summonerName, provideKey()));
    }

    @Override
    public SummonerInfo getSummonerByPuuId(final String puuId) {
        return summonerInfoMapper.fromSummonerInfoDto(eun1RiotClient.getSummonerByPuuId(puuId,provideKey()));
    }

    @Override
    public String getLatestLoLVersion() {
        String[] version = dDragonClient.getLolVersions();
        return version.length > 0 ? version[0] : null;
    }

    @Override
    public List<Rank> getLeagueV4Info(final String summonerId) {
        return rankMapper.mapToRankListFromRankDtoList(eun1RiotClient.getLeagueV4(summonerId, provideKey()));
    }

    @Override
    public List<Champion> getChampionsByPuuId(final String puuId) {
        return championMapper.mapToChampionListFromChampionDtoList(eun1RiotClient.getChampions(puuId, provideKey()));
    }

    @Override
    public List<String> getMatchesByPuuIdAndCount(final String puuId, final int count) {
        return europeRiotClient.getMatchesByPuuIdAndCount(puuId, count, provideKey());
    }

    @Override
    public List<LeagueInfo> getLeagueInfoListBySummonerId(final String summonerId) {
        return leagueMapper.mapToLeagueInfoListFromLeagueInfoDtoList(eun1RiotClient.getLeagueInfoBySummonerId(summonerId, provideKey()));
    }

    @Override
    public JsonNode getExampleSummonerNameFromExistingGame() {
        return eun1RiotClient.getExampleSummonerNameFromRandomExistingGame(provideKey());
    }

    @Override
    public JsonNode getAllChampionsDependsOnLoLVersion(final String latestLoLVersion) {
        return dDragonClient.getChampionById(latestLoLVersion);
    }

    @Override
    public JsonNode getInfoAboutMatchById(final String matchId) {
        return europeRiotClient.getInfoAboutMatchById(matchId, provideKey());
    }

    @Override
    public JsonNode getMatchInfoBySummonerId(final String id) {
        return eun1RiotClient.getMatchInfoBySummonerId(id, provideKey());
    }

    @Override
    public JsonNode getSummonerSpells(final String latestLoLVersion) {
        return dDragonClient.getSummonerSpells(latestLoLVersion);
    }

    @Override
    public SummonerInfo getSummonerFromAccountData(final String puuId) {
        return europeRiotClient.getSummonerByPuuIdFromAccountData(puuId,provideKey());
    }
}
