package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerDto {

    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private int profileIconId;
    private int summonerLevel;
    private List<RankDto> ranks;
    private String mainChamp;
    private String rankFlexColor;
    private String rankSoloColor;
    private String versionLoL;
}
