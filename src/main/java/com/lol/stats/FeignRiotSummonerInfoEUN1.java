package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "SummonerInfo", url = "${api.riot.url}")
interface FeignRiotSummonerInfoEUN1 {

    @GetMapping("/summoner/v4/summoners/by-name/{summonerName}?api_key={apiKey}")
    JsonNode getSummonerByName(@PathVariable String summonerName, @PathVariable final String apiKey);

    @GetMapping("/summoner/v4/summoners/by-puuid/{puuId}?api_key={apiKey}")
    JsonNode getSummonerByPuuid(@PathVariable String puuId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getActiveGameInfo(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getLeagueV4(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/champion-mastery/v4/champion-masteries/by-puuid/{puuId}/top?count=1&api_key={apiKey}")
    JsonNode getMainChampions(@PathVariable String puuId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getMatchInfoBySummonerId(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getLeagueInfoBySummonerId(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/featured-games?api_key={apiKey}")
    JsonNode getExampleSummonerNameFromRandomExistingGame(@PathVariable final String apiKey);
}
