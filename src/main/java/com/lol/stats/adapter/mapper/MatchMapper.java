package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.BannedChampionDto;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.MatchSummonerDto;
import com.lol.stats.model.BannedChampion;
import com.lol.stats.model.Match;
import com.lol.stats.model.MatchInfo;
import com.lol.stats.model.MatchSummoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchMapper {

    private final LeagueMapper leagueMapper;
    private final ChampMatchMapper champMatchMapper;

    public MatchDto toDto(final Match match) {
        return new MatchDto(
                match.getId(),
                match.getAccountId(),
                match.getPuuid(),
                match.getName(),
                match.getSummonerLevel(),
                leagueMapper.toDto(match.getLeagueInfo()),
                match.getRank(),
                match.getRankColor(),
                match.getWins(),
                match.getLosses(),
                champMatchMapper.toDtoList(match.getMatches())
        );
    }
}
