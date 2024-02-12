package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordMatchInfo;
import com.lol.stats.model.MatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchInfoMapper {

    private final BannedChampionMapper bannedChampionMapper;
    private final MatchSummonerMapper matchSummonerMapper;

    public RecordMatchInfo toDto(final MatchInfo matchInfo) {
        return new RecordMatchInfo(
                matchInfo.getUserTeam(),
                matchInfo.getGameMode(),
                bannedChampionMapper.toDtoList(matchInfo.getBannedChampions()),
                matchSummonerMapper.toDtoList(matchInfo.getSummoners())
        );
    }
}
