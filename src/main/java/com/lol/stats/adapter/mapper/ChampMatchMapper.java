package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordChampMatch;
import com.lol.stats.model.ChampMatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChampMatchMapper {
    public List<RecordChampMatch> toDtoList(final List<ChampMatch> matches) {
        return matches.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RecordChampMatch mapToDto(final ChampMatch champMatch) {
        return new RecordChampMatch(
                champMatch.getMatchId(),
                champMatch.getMatchChampName(),
                champMatch.getChampionId(),
                champMatch.getAssists(),
                champMatch.getKda(),
                champMatch.getDeaths(),
                champMatch.getKills(),
                champMatch.getLane(),
                champMatch.getDealtDamage(),
                champMatch.isWin(),
                champMatch.getWinColor(),
                champMatch.getTeamId()
        );
    }
}
