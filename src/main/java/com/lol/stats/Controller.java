package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
class Controller {

    private final RiotFacade riotFacade;

    Controller(RiotFacade riotFacade) {
        this.riotFacade = riotFacade;
    }

    @GetMapping
    JsonNode getSummonerByName(@RequestParam String summonerName) {
       return riotFacade.getSummonerInfoByName(summonerName);
    }

    @GetMapping("/champion")
    String getChampionById(@RequestParam String championId) throws IOException {
        return riotFacade.getChampionById(championId);
    }

    @GetMapping("/matches")
    JsonNode getAllSummonerMatchesByName(
            @RequestParam String summonerName,
            @RequestParam int count) throws IOException {
        return riotFacade.getAllSummonerMatchesByName(summonerName,count);
    }

    @GetMapping("/match")
    JsonNode getInfoAboutMatchById(@RequestParam String matchId) {
        return riotFacade.getInfoAboutMatchById(matchId);
    }
}
