package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "SummonerInfo", url = "${api.riot.url}/summoner/v4/summoners/")
interface FeignRiotSummonerInfo {

    @GetMapping("by-name/{summonerName}?api_key=${api.key}")
    JsonNode getSummonerByName(@PathVariable String summonerName);

    @GetMapping("by-puuid/{puuid}?api_key=${api.key}")
    JsonNode getSummonerByPuuid(@PathVariable String puuid);
}
