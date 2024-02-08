package com.lol.stats.dto;

import com.lol.stats.model.BannedChampion;
import com.lol.stats.model.PreviousMatchSummoner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviousMatchInfoDto {
    private List<TeamObjectiveDto> teamObjective = new ArrayList<>();
    private int timeInSeconds;
    private String matchId;
    private List<BannedChampion> bannedChampions = new ArrayList<>();
    private List<PreviousMatchSummoner> summoners = new ArrayList<>();
}