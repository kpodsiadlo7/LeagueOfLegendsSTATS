package com.lol.stats.dto;

import java.util.List;

public record MatchDto(String id, String accountId, String puuid, String name, int summonerLevel,
                       LeagueInfoDto leagueInfo, String rank, String rankColor, int wins, int losses,
                       List<ChampMatchDto> matches) {
}
