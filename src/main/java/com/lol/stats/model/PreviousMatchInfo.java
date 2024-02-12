package com.lol.stats.model;

import com.lol.stats.dto.RecordTeamObjective;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviousMatchInfo {
    private List<RecordTeamObjective> teamObjective = new ArrayList<>();
    private int timeInSeconds;
    private String matchId;
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private List<PreviousMatchSummoner> summoners = new ArrayList<>();
}
