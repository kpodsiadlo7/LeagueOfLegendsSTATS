package com.lol.stats.dto;

public record PreviousMatchSummonerDto(String matchId, String matchChampName, int championId, int assists, int kda,
                                       int deaths, int kills, String lane, int dealtDamage, boolean win, int teamId,
                                       String puuId, String summonerName, String rank, String rankColor) {
}
