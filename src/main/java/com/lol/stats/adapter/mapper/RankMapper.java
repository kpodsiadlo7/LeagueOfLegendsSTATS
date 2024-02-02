package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RankDto;
import com.lol.stats.model.Rank;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankMapper {
    public List<Rank> mapToRankListFromRankDtoList(final List<RankDto> rankDto) {
        return rankDto.stream()
                .map(this::mapToRankFromRankDto)
                .collect(Collectors.toList());
    }

    public Rank mapToRankFromRankDto(final RankDto rankDto) {
        return Rank.builder()
                .leagueId(rankDto.getLeagueId())
                .queueType(rankDto.getQueueType())
                .tier(rankDto.getTier())
                .rank(rankDto.getRank())
                .leaguePoints(rankDto.getLeaguePoints())
                .wins(rankDto.getWins())
                .losses(rankDto.getLosses()).build();
    }

    public List<RankDto> mapToRankDtoListFromRankList(final List<Rank> ranks) {
        return ranks.stream()
                .map(this::mapToRankDtoFromRank)
                .collect(Collectors.toList());
    }

    private RankDto mapToRankDtoFromRank(final Rank rank) {
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
