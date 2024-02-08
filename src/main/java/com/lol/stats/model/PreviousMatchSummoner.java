package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousMatchSummoner {
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

    /* data from summoner info */
    private String puuId;
    private String summonerName;
    private String rank;
    private String rankColor;
}
