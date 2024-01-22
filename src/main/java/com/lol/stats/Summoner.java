package com.lol.stats;

import lombok.Setter;

@Setter
class Summoner {
    private String summonerName;
    private int wardsPlaced;
    private int tripleKills;
    private int quadraKills;
    private boolean win;
    private int assists;
    private int assistMePings;

    private int controlWardsPlaced;
    private float kda;
    private int soloKills;
    private int kills;
    private int quickSoloKills;

    private int killingSprees;
    private int killsNearEnemyTurret;
    private int killsUnderOwnTurret;
    private int laneMinionsFirst10Minutes; // jeżeli jungler to kradnie na linii
    private int multiKillOneSpell;
    private int multikills;
    private int perfectGame;
    private int playedChampSelectPosition;

    private String lane;
    private int pentaKills;
    private int totalDamageDealtToChampions;
    private int totalDamageTaken;

    //JUNGLER
    private int alliedJungleMonsterKills;
    private int enemyJungleMonsterKills;
    private int jungleCsBefore10Minutes;
    private int killsOnLanersEarlyJungleAsJungler; // czy gankuje?

}
