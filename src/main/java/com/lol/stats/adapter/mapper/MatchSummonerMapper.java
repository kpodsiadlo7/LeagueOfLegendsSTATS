package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.MatchSummonerDto;
import com.lol.stats.model.MatchSummoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchSummonerMapper {
    List<MatchSummonerDto> toDtoList(final List<MatchSummoner> summoners) {
        return summoners.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    MatchSummonerDto toDto(final MatchSummoner matchSummoner) {
        return new MatchSummonerDto(
                matchSummoner.getPuuid(),
                matchSummoner.getTeamId(),
                matchSummoner.getChampionId(),
                matchSummoner.getSummonerName(),
                matchSummoner.getSummonerId(),
                matchSummoner.getRank(),
                matchSummoner.getRankColor(),
                matchSummoner.getChampName(),
                matchSummoner.getSpellName1(),
                matchSummoner.getSpellName2()
        );
    }
}
