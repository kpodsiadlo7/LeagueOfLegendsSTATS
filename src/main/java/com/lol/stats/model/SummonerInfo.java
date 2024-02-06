package com.lol.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummonerInfo {

    private String id;
    private String accountId;
    private String puuid;
    private String tagLine;
    private String name;
    private String gameName;
    private int profileIconId;
    private int summonerLevel;
}