package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordMatch;
import com.lol.stats.model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchMapper {

    private final ChampMatchMapper champMatchMapper;

    public RecordMatch toDto(final Match match) {
        return new RecordMatch(
                match.getId(),
                match.getAccountId(),
                match.getPuuid(),
                match.getName(),
                match.getSummonerLevel(),
                match.getLeagueInfo() == null ? null : match.getLeagueInfo(),
                match.getRank(),
                match.getRankColor(),
                match.getWins(),
                match.getLosses(),
                champMatchMapper.toDtoList(match.getMatches())
        );
    }
}
