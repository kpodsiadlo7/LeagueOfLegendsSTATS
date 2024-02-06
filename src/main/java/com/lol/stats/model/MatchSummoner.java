package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchSummoner {
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
