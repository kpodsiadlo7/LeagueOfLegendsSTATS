package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "RiotActiveGame", url = "${api.riot.url}/spectator/v4/active-games/by-summoner/")
interface FeignRiotActiveGame {

    @GetMapping("{summonerId}?api_key=${api.key}")
    JsonNode getActiveGameInfo(@PathVariable String summonerId);
}
