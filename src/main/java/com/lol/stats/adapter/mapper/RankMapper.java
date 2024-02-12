package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordRank;
import com.lol.stats.model.Rank;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankMapper {
    public List<Rank> fromDtoList(final List<RecordRank> recordRank) {
        return recordRank.stream()
                .map(this::fromDto)
                .collect(Collectors.toList());
    }

    public Rank fromDto(final RecordRank recordRank) {
        return Rank.builder()
                .leagueId(recordRank.leagueId())
                .queueType(recordRank.queueType())
                .tier(recordRank.tier())
                .rank(recordRank.rank())
                .leaguePoints(recordRank.leaguePoints())
                .wins(recordRank.wins())
                .losses(recordRank.losses()).build();
    }

    public List<RecordRank> toDtoList(final List<Rank> ranks) {
        return ranks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RecordRank toDto(final Rank rank) {
        return new RecordRank(
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
