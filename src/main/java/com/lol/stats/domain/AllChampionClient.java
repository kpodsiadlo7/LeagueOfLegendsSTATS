package com.lol.stats.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "RiotAllChampions", url = "https://ddragon.leagueoflegends.com/")
public interface AllChampionClient {

    @GetMapping("/api/versions.json")
    String[] getLolVersions();

    @GetMapping("/cdn/{latestVersion}/data/en_US/champion.json")
    JsonNode getChampionById(@PathVariable String latestVersion);

    @GetMapping("/cdn/{latestVersion}/data/en_US/summoner.json")
    JsonNode getSummonerSpells(@PathVariable String latestVersion);
}
