package com.lol.stats.adapter.web;

import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.SummonerDto;
import com.lol.stats.model.ChampMatch;
import com.lol.stats.model.PreviousMatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
@CrossOrigin(origins = "http://127.0.0.1:3000/")
public class Controller {

    private final RiotFacade riotFacade;

    @GetMapping
    SummonerDto getSummonerByName(@RequestParam final String summonerName) {
        return riotFacade.getSummonerInfoByName(summonerName);
    }

    @GetMapping("/matches")
    List<String> getSummonerMatchesByNameAndCount(
            @RequestParam final String summonerName,
            @RequestParam final int count) throws IOException, InterruptedException {
        return riotFacade.getSummonerMatchesByNameAndCount(summonerName, count);
    }

    @GetMapping("/matchInfo")
    MatchInfoDto getMatchInfoBySummonerName(@RequestParam final String summonerName) {
        return riotFacade.getInfoAboutAllSummonerInActiveGame(summonerName);
    }

    @GetMapping("/last3matches")
    MatchDto getLast3MatchesBySummonerName(@RequestParam final String puuId) throws InterruptedException {
        return riotFacade.getLastMatchesByPuuIdAndCounts(puuId,20,3);
    }

    @GetMapping("/last20matches")
    MatchDto getLast20MatchesBySummonerName(@RequestParam final String puuId) throws InterruptedException {
        return riotFacade.getLastMatchesByPuuIdAndCounts(puuId,50,20);
    }

    @GetMapping("/randomMatch")
    String getRandomSummonerNameFromExistingGame() {
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }

    @GetMapping("/previous-match")
    PreviousMatchInfo getPreviousMatchByMatchId(@RequestParam final String matchId){
        return riotFacade.getPreviousMatchByMatchId(matchId);
    }
}
