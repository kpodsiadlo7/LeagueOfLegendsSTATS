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
    private String name;
    private int profileIconId;
    private int summonerLevel;
}