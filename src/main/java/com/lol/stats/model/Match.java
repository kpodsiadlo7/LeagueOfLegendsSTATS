package com.lol.stats.model;

import com.lol.stats.dto.LeagueInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
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
    private List<ChampMatch> matches;
}
