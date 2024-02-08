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
public class MatchInfo {
    private String userTeam;
    private String gameMode;
    private int timeInSeconds;
    private List<BannedChampion> bannedChampions;
    private List<MatchSummoner> summoners;
}
