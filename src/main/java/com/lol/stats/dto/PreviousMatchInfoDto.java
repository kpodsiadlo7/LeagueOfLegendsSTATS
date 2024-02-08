package com.lol.stats.dto;

import com.lol.stats.model.BannedChampion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviousMatchInfoDto {
    private List<ChampMatchDto> matchList;
    private List<TeamObjectiveDto> teamObjective = new ArrayList<>();
    private MatchInfoDto matchInfo;
    private List<BannedChampion> bannedChampions = new ArrayList<>();
}