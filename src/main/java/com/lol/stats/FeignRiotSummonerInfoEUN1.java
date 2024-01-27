package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "SummonerInfo", url = "${api.riot.url}")
interface FeignRiotSummonerInfoEUN1 {

    @GetMapping("/summoner/v4/summoners/by-name/{summonerName}?api_key=${api.key}")
    JsonNode getSummonerByName(@PathVariable String summonerName);

    @GetMapping("/summoner/v4/summoners/by-puuid/{puuId}?api_key=${api.key}")
    JsonNode getSummonerByPuuid(@PathVariable String puuId);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key=${api.key}")
    JsonNode getActiveGameInfo(@PathVariable String summonerId);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key=${api.key}")
    JsonNode getLeagueV4(@PathVariable String summonerId);

    @GetMapping("/champion-mastery/v4/champion-masteries/by-puuid/{puuId}/top?count=1&api_key=${api.key}")
    JsonNode getMainChampions(@PathVariable String puuId);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key=${api.key}")
    JsonNode getMatchInfoBySummonerId(@PathVariable String summonerId);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key=${api.key}")
    JsonNode getLeagueInfoBySummonerId(@PathVariable String summonerId);
}
