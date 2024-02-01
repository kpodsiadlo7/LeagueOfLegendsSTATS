package com.lol.stats.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
//@CrossOrigin(origins = "http://127.0.0.1:3000/")
public class Controller {

    private final RiotFacade riotFacade;
    private final SummonerMapper summonerMapper;

    @GetMapping
    SummonerDto getSummonerByName(@RequestParam String summonerName) throws InterruptedException {
       return summonerMapper.toSummonerDto(riotFacade.getSummonerInfoByName(summonerName));
    }
/*
    @GetMapping("/champion")
    String getChampionById(@RequestParam String championId) throws IOException {
        return riotFacade.getChampionById(championId);
    }

    @GetMapping("/matches")
    JsonNode getSummonerMatchesByNameAndCount(
            @RequestParam String summonerName,
            @RequestParam int count) throws IOException, InterruptedException {
        return riotFacade.getSummonerMatchesByNameAndCount(summonerName,count);
    }

    @GetMapping("/matchInfo")
    JsonNode getMatchInfoBySummonerName(@RequestParam String summonerName){
        return riotFacade.getInfoAboutAllSummonerInActiveGame(summonerName);
    }

    @GetMapping("/match")
    JsonNode getInfoAboutMatchById(@RequestParam String matchId) {
        return riotFacade.getInfoAboutMatchById(matchId);
    }

    @GetMapping("/last3matches")
    JsonNode getLast10MatchesBySummonerName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getLast3MatchesBySummonerName(summonerName);
    }

    @GetMapping("/last20matches")
    JsonNode getLast20MatchesBySummonerName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getLast20MatchesBySummonerName(summonerName);
    }


    @GetMapping("/randomMatch")
    String  getRandomSummonerNameFromExistingGame(){
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }

 */
}
