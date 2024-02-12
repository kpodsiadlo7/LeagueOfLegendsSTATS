package com.lol.stats.dto;

import com.lol.stats.model.BannedChampion;
import com.lol.stats.model.PreviousMatchSummoner;

import java.util.List;

public record RecordPreviousMatchInfo(List<RecordTeamObjective> teamObjective, int timeInSeconds, String matchId,
                                      List<BannedChampion> bannedChampions, List<PreviousMatchSummoner> summoners) {
}