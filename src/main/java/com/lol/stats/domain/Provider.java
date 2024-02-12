package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.dto.RecordChampion;
import com.lol.stats.dto.RecordLeagueInfo;
import com.lol.stats.model.Rank;
import com.lol.stats.model.SummonerInfo;

import java.util.List;

public interface Provider {
    String provideKey();

    SummonerInfo getSummonerInfo(final String summonerName);
    SummonerInfo getSummonerByPuuId(final String puuId);

    String getLatestLoLVersion();

    List<Rank> getLeagueV4Info(final String summonerId);

    List<RecordChampion> getChampionsByPuuId(final String puuId);

    List<String> getMatchesByPuuIdAndCount(final String puuId, final int count);

    List<RecordLeagueInfo> getLeagueInfoListBySummonerId(final String summonerId);

    JsonNode getExampleSummonerNameFromExistingGame();

    JsonNode getAllChampionsDependsOnLoLVersion(final String latestLoLVersion);

    JsonNode getInfoAboutMatchById(final String matchId);

    JsonNode getMatchInfoBySummonerId(final String id);

    JsonNode getSummonerSpells(final String latestLoLVersion);

    SummonerInfo getSummonerFromAccountData(final String puuId);
}
