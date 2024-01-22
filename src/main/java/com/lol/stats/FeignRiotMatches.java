package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "RiotMatches", url = "https://europe.api.riotgames.com/lol/match/v5/matches/")
interface FeignRiotMatches {
    @GetMapping("by-puuid/{puuid}/ids?start=0&count={count}&api_key=${api.key}")
    JsonNode getAllMatchesByPuuidAndCount(@PathVariable String puuid, @PathVariable int count);

    @GetMapping("{matchId}?api_key=${api.key}")
    JsonNode getInfoAboutMatchById(@PathVariable String matchId);
}
