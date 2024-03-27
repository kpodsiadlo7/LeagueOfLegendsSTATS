package com.lol.stats.adapter.web;

import com.lol.stats.adapter.mapper.SummonerMapper;
import com.lol.stats.domain.RiotFacade;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.PreviousMatchInfoDto;
import com.lol.stats.dto.SummonerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final SummonerMapper summonerMapper;

    @GetMapping("/summoner/{summonerName}")
    SummonerDto getSummonerByName(@PathVariable final String summonerName) {
        return summonerMapper.toDto(riotFacade.getSummonerInfoByName(summonerName));
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
    @GetMapping("/lastMatches")
    MatchDto getLastMatchesByPuuIdAndCount(@RequestParam final String puuId,
                                           @RequestParam final int matchesListCount,
                                           @RequestParam final int rankedCount) throws InterruptedException {
        return riotFacade.getLastMatchesByPuuIdAndCounts(puuId, matchesListCount, rankedCount);
    }

    @GetMapping("/randomMatch")
    String getRandomSummonerNameFromExistingGame() {
        return riotFacade.getRandomSummonerNameFromExistingGame();
    }

    @GetMapping("/previous-match")
    PreviousMatchInfoDto getPreviousMatchByMatchId(@RequestParam final String matchId) {
        return riotFacade.getPreviousMatchByMatchId(matchId);
    }

    @GetMapping("/warmup")
    ResponseEntity<?> warmup() {
        log.info("Warmup at " + LocalDateTime.now());
        return ResponseEntity.ok().build();
    }
}
