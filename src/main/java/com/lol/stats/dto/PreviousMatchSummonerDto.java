package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviousMatchSummonerDto {
    private String matchId;
    private String matchChampName;
    private int championId;
    private int assists;
    private int kda;
    private int deaths;
    private int kills;
    private String lane;
    private int dealtDamage;
    private boolean win;
    private int teamId;
    private String puuId;
    private String summonerName;
    private String rank;
    private String rankColor;
}
