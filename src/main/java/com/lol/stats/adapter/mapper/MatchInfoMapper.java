package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.model.MatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchInfoMapper {

    private final BannedChampionMapper bannedChampionMapper;
    private final MatchSummonerMapper matchSummonerMapper;

    public MatchInfoDto toDto(final MatchInfo matchInfo) {
        return new MatchInfoDto(
                matchInfo.getUserTeam(),
                matchInfo.getGameMode(),
                matchInfo.getTimeInSeconds(),
                bannedChampionMapper.toDtoList(matchInfo.getBannedChampions()),
                matchSummonerMapper.toDtoList(matchInfo.getSummoners())
        );
    }
}
