package com.lol.stats.adapter.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.adapter.mapper.RankMapper;
import com.lol.stats.adapter.mapper.SummonerInfoMapper;
import com.lol.stats.domain.SummonerProvider;
import com.lol.stats.domain.client.DDragonClient;
import com.lol.stats.domain.client.EUN1RiotClient;
import com.lol.stats.domain.client.EuropeRiotClient;
import com.lol.stats.dto.ChampionDto;
import com.lol.stats.dto.LeagueInfoDto;
import com.lol.stats.model.Rank;
import com.lol.stats.model.SummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummonerProviderImpl implements SummonerProvider {

    @Value("${api.key}")
    private String API_KEY;
    private final EUN1RiotClient eun1RiotClient;
    private final SummonerInfoMapper summonerInfoMapper;
    private final RankMapper rankMapper;
    private final EuropeRiotClient europeRiotClient;
    private final DDragonClient dDragonClient;

    @Override
    public String provideKey() {
        try {
            String apiKeyFilePath = API_KEY;
            Path path = Paths.get(apiKeyFilePath);
            byte[] apiKeyBytes = Files.readAllBytes(path);
            return new String(apiKeyBytes);
        } catch (IOException e) {
            log.error(e.toString());
        }
        return "CAN'T FETCH API KEY";
    }

    @Override
    public SummonerInfo getSummonerInfo(final String summonerName) {
        if (summonerName == null || summonerName.isEmpty()) return new SummonerInfo();

        // if we are looking for by name with #hash, we need to use europeRiotClient
        String[] nameAndHash = summonerName.split("#");
        if (nameAndHash.length == 2) {
            // nameAndHash[0] <- summonerName, nameAndHash[1] <- summonerHash
            return summonerInfoMapper.fromDto(
                    europeRiotClient.getSummonerByNameAndHash(nameAndHash[0], nameAndHash[1], provideKey()));
        }
        // we are looking for by name without #hash
        return summonerInfoMapper.fromDto(eun1RiotClient.getSummonerByName(summonerName, provideKey()));
    }

    @Override
    public SummonerInfo getSummonerByPuuId(final String puuId) {
        return summonerInfoMapper.fromDto(eun1RiotClient.getSummonerByPuuId(puuId, provideKey()));
    }

    @Override
    public String getLatestLoLVersion() {
        String[] version = dDragonClient.getLolVersions();
        return version != null ? Arrays.stream(version).findFirst().orElse(null) : null;
    }

    @Override
    public List<Rank> getLeagueV4Info(final String summonerId) {
        return rankMapper.fromDtoList(eun1RiotClient.getLeagueV4(summonerId, provideKey()));
    }

    @Override
    public List<ChampionDto> getChampionsByPuuId(final String puuId) {
        return eun1RiotClient.getChampions(puuId, provideKey());
    }

    @Override
    public List<String> getMatchesByPuuIdAndCount(final String puuId, final int count) {
        return europeRiotClient.getMatchesByPuuIdAndCount(puuId, count, provideKey());
    }

    @Override
    public List<LeagueInfoDto> getLeagueInfoListBySummonerId(final String summonerId) {
        return eun1RiotClient.getLeagueInfoBySummonerId(summonerId, provideKey());
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
        return europeRiotClient.getSummonerByPuuIdFromAccountData(puuId, provideKey());
    }
}
