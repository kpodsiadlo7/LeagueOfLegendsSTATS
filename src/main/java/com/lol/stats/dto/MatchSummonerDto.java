package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchSummonerDto {
    private String puuid;
    private int teamId;
    private int championId;
    private String summonerName;
    private String summonerId;
    private String rank;
    private String rankColor;
    private String champName;
    private String spellName1;
    private String spellName2;
}