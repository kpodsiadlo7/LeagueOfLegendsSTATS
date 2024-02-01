package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeagueInfo {
    private String leagueId;
    private String queueType;
    private String tier;
    private String rank;
    private String summonerId;
    private String summonerName;
    private int leaguePoints;
    private int wins;
    private int losses;
    private boolean veteran;
    private boolean inactive;
    private boolean freshBlood;
    private boolean hotStreak;
}
