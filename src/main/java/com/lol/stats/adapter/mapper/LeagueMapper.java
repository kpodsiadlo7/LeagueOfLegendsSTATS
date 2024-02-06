package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.LeagueInfoDto;
import com.lol.stats.model.LeagueInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeagueMapper {
    public List<LeagueInfo> fromDtoList(final List<LeagueInfoDto> leagueInfoDtoList) {
        return leagueInfoDtoList.stream()
                .map(this::fromDto)
                .collect(Collectors.toList());
    }

    LeagueInfo fromDto(final LeagueInfoDto leagueInfoDto) {
        return LeagueInfo.builder()
                .leagueId(leagueInfoDto.getLeagueId())
                .queueType(leagueInfoDto.getQueueType())
                .tier(leagueInfoDto.getTier())
                .rank(leagueInfoDto.getRank())
                .summonerId(leagueInfoDto.getSummonerId())
                .summonerName(leagueInfoDto.getSummonerName())
                .leaguePoints(leagueInfoDto.getLeaguePoints())
                .wins(leagueInfoDto.getWins())
                .losses(leagueInfoDto.getLosses()).build();
    }

    public LeagueInfoDto toDto(final LeagueInfo leagueInfo) {
        return new LeagueInfoDto(
                leagueInfo.getLeagueId(),
                leagueInfo.getQueueType(),
                leagueInfo.getTier(),
                leagueInfo.getRank(),
                leagueInfo.getSummonerId(),
                leagueInfo.getSummonerName(),
                leagueInfo.getLeaguePoints(),
                leagueInfo.getWins(),
                leagueInfo.getLosses()
        );
    }
}
