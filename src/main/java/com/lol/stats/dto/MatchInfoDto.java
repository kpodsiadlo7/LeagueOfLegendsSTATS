package com.lol.stats.dto;

import java.util.List;

public record MatchInfoDto(String userTeam, String gameMode, List<BannedChampionDto> bannedChampions,
                           List<MatchSummonerDto> summoners) {
}
