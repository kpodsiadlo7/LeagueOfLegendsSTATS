package com.lol.stats.dto;

public record RecordLeagueInfo(String leagueId, String queueType, String tier, String rank, String summonerId,
                               String summonerName, int leaguePoints, int wins, int losses) {
}
