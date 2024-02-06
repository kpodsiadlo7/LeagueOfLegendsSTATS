package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RankDto;
import com.lol.stats.model.Rank;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankMapper {
    public List<Rank> fromDtoList(final List<RankDto> rankDto) {
        return rankDto.stream()
                .map(this::fromDto)
                .collect(Collectors.toList());
    }

    public Rank fromDto(final RankDto rankDto) {
        return Rank.builder()
                .leagueId(rankDto.getLeagueId())
                .queueType(rankDto.getQueueType())
                .tier(rankDto.getTier())
                .rank(rankDto.getRank())
                .leaguePoints(rankDto.getLeaguePoints())
                .wins(rankDto.getWins())
                .losses(rankDto.getLosses()).build();
    }

    public List<RankDto> toDtoList(final List<Rank> ranks) {
        return ranks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RankDto toDto(final Rank rank) {
        return new RankDto(
                rank.getLeagueId(),
                rank.getQueueType(),
                rank.getTier(),
                rank.getRank(),
                rank.getLeaguePoints(),
                rank.getWins(),
                rank.getLosses()
        );
    }
}
