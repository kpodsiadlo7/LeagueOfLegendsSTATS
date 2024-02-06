package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummonerInfoDto {

    private String id;
    private String accountId;
    private String puuid;
    private String tagLine;
    private String name;
    private String gameName;
    private int profileIconId;
    private int summonerLevel;
}
