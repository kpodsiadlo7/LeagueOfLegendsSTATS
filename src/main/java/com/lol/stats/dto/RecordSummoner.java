package com.lol.stats.dto;

import java.util.List;

public record RecordSummoner(String id, String accountId, String puuid, String name, int profileIconId,
                             int summonerLevel, List<RecordRank> ranks, String mainChamp, String rankFlexColor,
                             String rankSoloColor, String versionLoL) {

}
