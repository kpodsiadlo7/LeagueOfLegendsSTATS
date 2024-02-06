package com.lol.stats.model;

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
    private List<ChampMatch> matchList;
    private List<TeamObjective> teamObjective = new ArrayList<>();
    private MatchInfo matchInfo;
}
