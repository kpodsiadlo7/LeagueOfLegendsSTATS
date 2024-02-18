package com.lol.stats.dto;

public record ChampMatchDto(String matchId, String matchChampName, int championId, int assists, int kda, int deaths,
                            int kills, String lane, int dealtDamage, boolean win, String winColor, int teamId) {
}
