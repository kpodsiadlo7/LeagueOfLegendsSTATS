package com.lol.stats.adapter;

import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.SummonerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
//@CrossOrigin(origins = "http://127.0.0.1:3000/")
public class Controller {

    private final RiotFacade riotFacade;

    @GetMapping
    SummonerDto getSummonerByName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getSummonerInfoByName(summonerName);
    }

    @GetMapping("/matches")
    List<String> getSummonerMatchesByNameAndCount(
            @RequestParam String summonerName,
            @RequestParam int count) throws IOException, InterruptedException {
        return riotFacade.getSummonerMatchesByNameAndCount(summonerName, count);
    }

    @GetMapping("/matchInfo")
    MatchInfoDto getMatchInfoBySummonerName(@RequestParam String summonerName) {
        return riotFacade.getInfoAboutAllSummonerInActiveGame(summonerName);
    }

    @GetMapping("/last3matches")
    MatchDto getLast3MatchesBySummonerName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getLast3MatchesBySummonerName(summonerName);
    }

    @GetMapping("/last20matches")
    MatchDto getLast20MatchesBySummonerName(@RequestParam String summonerName) throws InterruptedException {
        return riotFacade.getLast20MatchesBySummonerName(summonerName);
    }

    @GetMapping("/randomMatch")
    String getRandomSummonerNameFromExistingGame() {
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }
}
