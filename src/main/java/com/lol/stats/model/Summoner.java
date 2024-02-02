package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Summoner {
    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private int profileIconId;
    private int summonerLevel;
    private List<Rank> ranks;
    private String mainChamp;
    private String rankFlexColor;
    private String rankSoloColor;
    private String versionLoL;
}
