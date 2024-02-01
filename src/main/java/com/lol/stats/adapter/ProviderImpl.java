package com.lol.stats.adapter;

import com.lol.stats.domain.ClientLoLVersion;
import com.lol.stats.domain.Provider;
import com.lol.stats.domain.SummonerClient;
import com.lol.stats.model.Champion;
import com.lol.stats.model.Rank;
import com.lol.stats.model.SummonerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SummonerClient summonerClient;
    private final SummonerMapper summonerMapper;
    private final ChampionMapper championMapper;
    private final ClientLoLVersion clientLoLVersion;

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
    public SummonerInfo getSummonerInfo(String summonerName) {
        return summonerMapper.fromSummonerInfoDto(summonerClient.getSummonerByName(summonerName, provideKey()));
    }

    @Override
    public String getLatestLoLVersion() {
        return clientLoLVersion.getLolVersions()[0];
    }

    @Override
    public List<Rank> getLeagueV4Info(String summonerId) {
        return summonerMapper.fromListRankDto(summonerClient.getLeagueV4(summonerId, provideKey()));
    }

    @Override
    public List<Champion> getChampionsByPuuId(String puuid) {
        return championMapper.fromChampionDtoList(summonerClient.getChampions(puuid, provideKey()));
    }
}
