package com.lol.stats.dto;

public record RecordMatchSummoner(String puuid, int teamId, int championId, String summonerName, String summonerId,
                                  String rank, String rankColor, String champName, String spellName1,
                                  String spellName2) {
}