package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {
    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private int summonerLevel;
    private LeagueInfoDto leagueInfo;
    private String rank;
    private String rankColor;
    private int wins;
    private int losses;
    private List<ChampMatchDto> matches;
}
