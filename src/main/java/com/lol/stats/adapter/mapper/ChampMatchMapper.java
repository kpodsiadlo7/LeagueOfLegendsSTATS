package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.ChampMatchDto;
import com.lol.stats.model.ChampMatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChampMatchMapper {
    public List<ChampMatchDto> mapToChampMatchDtoListFromChampMatchList(final List<ChampMatch> matches) {
        return matches.stream()
                .map(this::mapToChampMatchDtoFromMatchChamp)
                .collect(Collectors.toList());
    }

    private ChampMatchDto mapToChampMatchDtoFromMatchChamp(final ChampMatch champMatch) {
        return new ChampMatchDto(
                champMatch.getMatchId(),
                champMatch.getMatchChampName(),
                champMatch.getChampionId(),
                champMatch.getAssists(),
                champMatch.getKda(),
                champMatch.getDeaths(),
                champMatch.getKills(),
                champMatch.getLane(),
                champMatch.getDealtDamage(),
                champMatch.getWin(),
                champMatch.getWinColor(),
                champMatch.getTeamId()
        );
    }


}
