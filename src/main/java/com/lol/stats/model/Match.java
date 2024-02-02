package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private int profileIconId;
    private int summonerLevel;
    private LeagueInfo leagueInfo;
    private String rank;
    private String rankColor;
    private int wins;
    private int losses;
    private List<ChampMatch> matches;
}
