package com.lol.stats.domain.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.dto.SummonerInfoDto;
import com.lol.stats.model.SummonerInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "RiotMatches", url = "${europe.riot.url}")
public interface EuropeRiotClient {
    @GetMapping("lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count={count}&api_key={apiKey}")
    List<String> getMatchesByPuuIdAndCount(@PathVariable final String puuid, @PathVariable int count, @PathVariable final String apiKey);

    @GetMapping("lol/match/v5/matches/{matchId}?api_key={apiKey}")
    JsonNode getInfoAboutMatchById(@PathVariable final String matchId, @PathVariable final String apiKey);

    @GetMapping("riot/account/v1/accounts/by-riot-id/{summonerName}/{summonerHash}?api_key={apiKey}")
    SummonerInfoDto getSummonerByNameAndHash(@PathVariable final String summonerName, @PathVariable final String summonerHash, @PathVariable  final String apiKey);

    @GetMapping("/riot/account/v1/accounts/by-puuid/{puuId}?api_key={apiKey}")
    SummonerInfo getSummonerByPuuIdFromAccountData(@PathVariable final String puuId, @PathVariable final String apiKey);
}
