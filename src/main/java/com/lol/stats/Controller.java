package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
//@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
    @CrossOrigin(origins = "http://127.0.0.1:3000/")
class Controller {

    private final RiotFacade riotFacade;

    Controller(RiotFacade riotFacade) {
        this.riotFacade = riotFacade;
    }

    @GetMapping
    JsonNode getSummonerByName(@RequestParam String summonerName) throws InterruptedException {
       return riotFacade.getSummonerInfoByName(summonerName);
    }

    @GetMapping("/champion")
    String getChampionById(@RequestParam String championId) throws IOException {
        return riotFacade.getChampionById(championId);
    }

    @GetMapping("/matches")
    JsonNode getAllSummonerMatchesByName(
            @RequestParam String summonerName,
            @RequestParam int count) throws IOException, InterruptedException {
        return riotFacade.getAllSummonerMatchesByName(summonerName,count);
    }

    @GetMapping("/matchInfo")
    JsonNode getMatchInfoBySummonerName(@RequestParam String summonerName){
        return riotFacade.getInfoAboutAllSummonerInActiveGame(summonerName);
    }

    @GetMapping("/match")
    JsonNode getInfoAboutMatchById(@RequestParam String matchId) {
        return riotFacade.getInfoAboutMatchById(matchId);
    }
}
