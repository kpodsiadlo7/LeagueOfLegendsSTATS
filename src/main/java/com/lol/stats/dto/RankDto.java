package com.lol.stats.dto;

public record RankDto(String leagueId, String queueType, String tier, String rank, int leaguePoints, int wins,
                      int losses) {
}
