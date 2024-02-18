package com.lol.stats.dto;

public record LeagueInfoDto(String leagueId, String queueType, String tier, String rank, String summonerId,
                            String summonerName, int leaguePoints, int wins, int losses) {
}
