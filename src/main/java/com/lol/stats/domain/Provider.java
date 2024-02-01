package com.lol.stats.domain;
import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.model.Champion;
import com.lol.stats.model.LeagueInfo;
import com.lol.stats.model.Rank;
import com.lol.stats.model.SummonerInfo;

import java.util.List;

public interface Provider {
    String provideKey();

    SummonerInfo getSummonerInfo(final String summonerName);

    String getLatestLoLVersion();

    List<Rank> getLeagueV4Info(final String summonerId);

    List<Champion> getChampionsByPuuId(final String puuid);

    List<String> getMatchesByPuuIdAndCount(final String puuid, final int count);

    List<LeagueInfo> getLeagueInfoListBySummonerId(String summonerId);

    JsonNode getExampleSummonerNameFromExistingGame();
}
