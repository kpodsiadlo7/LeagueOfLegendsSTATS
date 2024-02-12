package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchInfoDto {
    private String userTeam;
    private String gameMode;
    private List<RecordBannedChampion> bannedChampions;
    private List<MatchSummonerDto> summoners;
}
