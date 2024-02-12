package com.lol.stats.domain.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.dto.RecordChampion;
import com.lol.stats.dto.RecordLeagueInfo;
import com.lol.stats.dto.RecordRank;
import com.lol.stats.dto.RecordSummonerInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "SummonerInfo", url = "${eun1.riot.url}")
public interface EUN1RiotClient {

    @GetMapping("/summoner/v4/summoners/by-name/{summonerName}?api_key={apiKey}")
    RecordSummonerInfo getSummonerByName(@PathVariable String summonerName, @PathVariable final String apiKey);

    @GetMapping("/summoner/v4/summoners/by-puuid/{puuId}?api_key={apiKey}")
    RecordSummonerInfo getSummonerByPuuId(@PathVariable String puuId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getActiveGameInfo(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key={apiKey}")
    List<RecordRank> getLeagueV4(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/champion-mastery/v4/champion-masteries/by-puuid/{puuId}/top?count=1&api_key={apiKey}")
    List<RecordChampion> getChampions(@PathVariable String puuId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/active-games/by-summoner/{summonerId}?api_key={apiKey}")
    JsonNode getMatchInfoBySummonerId(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/league/v4/entries/by-summoner/{summonerId}?api_key={apiKey}")
    List<RecordLeagueInfo> getLeagueInfoBySummonerId(@PathVariable String summonerId, @PathVariable final String apiKey);

    @GetMapping("/spectator/v4/featured-games?api_key={apiKey}")
    JsonNode getExampleSummonerNameFromRandomExistingGame(@PathVariable final String apiKey);
}
