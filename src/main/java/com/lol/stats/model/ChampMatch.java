package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampMatch {
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
    private String winColor;
    private int teamId;
}
