package com.lol.stats.dto;

import java.util.List;

public record RecordMatch(String id, String accountId, String puuid, String name, int summonerLevel,
                          RecordLeagueInfo leagueInfo, String rank, String rankColor, int wins, int losses,
                          List<RecordChampMatch> matches) {
}
