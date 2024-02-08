package com.lol.stats.dto;

import com.lol.stats.model.BannedChampion;
import com.lol.stats.model.MatchSummoner;
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
    private List<BannedChampionDto> bannedChampions;
    private List<MatchSummonerDto> summoners;
}
