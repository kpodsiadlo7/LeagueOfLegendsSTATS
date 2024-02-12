package com.lol.stats.adapter.web;

import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.RecordMatch;
import com.lol.stats.dto.RecordMatchInfo;
import com.lol.stats.dto.RecordPreviousMatchInfo;
import com.lol.stats.dto.RecordSummoner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://kpodsiadlo7.github.io")
//@CrossOrigin(origins = "http://127.0.0.1:3000/")
public class Controller {

    private final RiotFacade riotFacade;

    @GetMapping
    RecordSummoner getSummonerByName(@RequestParam final String summonerName) {
        return riotFacade.getSummonerInfoByName(summonerName);
    }

    @GetMapping("/matches")
    List<String> getSummonerMatchesByNameAndCount(
            @RequestParam final String summonerName,
            @RequestParam final int count) throws IOException, InterruptedException {
        return riotFacade.getSummonerMatchesByNameAndCount(summonerName, count);
    }

    @GetMapping("/matchInfo")
    RecordMatchInfo getMatchInfoBySummonerName(@RequestParam final String summonerName) {
        return riotFacade.getInfoAboutAllSummonerInActiveGame(summonerName);
    }

    @GetMapping("/last3matches")
    RecordMatch getLast3MatchesBySummonerName(@RequestParam final String puuId) throws InterruptedException {
        return riotFacade.getLastMatchesByPuuIdAndCounts(puuId, 20, 3);
    }

    @GetMapping("/last20matches")
    RecordMatch getLast20MatchesBySummonerName(@RequestParam final String puuId) throws InterruptedException {
        return riotFacade.getLastMatchesByPuuIdAndCounts(puuId, 50, 20);
    }

    @GetMapping("/randomMatch")
    String getRandomSummonerNameFromExistingGame() {
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }

    @GetMapping("/previous-match")
    RecordPreviousMatchInfo getPreviousMatchByMatchId(@RequestParam final String matchId) {
        return riotFacade.getPreviousMatchByMatchId(matchId);
    }

    @GetMapping("/warmup")
    ResponseEntity<?> warmup() {
        log.info("Warmup at " + LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
