package com.lol.stats.adapter;

import com.lol.stats.domain.MatchClient;
import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
@CrossOrigin(origins = "http://127.0.0.1:3000/")
public class Controller {

    private final RiotFacade riotFacade;

    @GetMapping
    SummonerDto getSummonerByName(@RequestParam String summonerName) throws InterruptedException {
       return riotFacade.getSummonerInfoByName(summonerName);
    }
/*
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

 */

    @GetMapping("/last20matches")
    MatchDto getLast20MatchesBySummonerName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getLast20MatchesBySummonerName(summonerName);
    }

    @GetMapping("/randomMatch")
    String getRandomSummonerNameFromExistingGame(){
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }
}
