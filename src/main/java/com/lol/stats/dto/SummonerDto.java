package com.lol.stats.dto;

import java.util.List;

public record SummonerDto(String id, String accountId, String puuid, String name, int profileIconId,
                          int summonerLevel, List<RankDto> ranks, String mainChamp, String rankFlexColor,
                          String rankSoloColor, String versionLoL) {

}
