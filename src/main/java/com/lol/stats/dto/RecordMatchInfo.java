package com.lol.stats.dto;

import java.util.List;

public record RecordMatchInfo(String userTeam, String gameMode, List<RecordBannedChampion> bannedChampions,
                              List<RecordMatchSummoner> summoners) {
}
