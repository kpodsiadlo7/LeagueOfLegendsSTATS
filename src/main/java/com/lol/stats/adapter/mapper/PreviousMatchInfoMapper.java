package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.PreviousMatchInfoDto;
import com.lol.stats.model.PreviousMatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreviousMatchInfoMapper {

    private final TeamObjectiveMapper teamObjectiveMapper;

    public PreviousMatchInfoDto toDto(final PreviousMatchInfo previousMatchInfo) {
        return new PreviousMatchInfoDto(
                teamObjectiveMapper.toDtoList(previousMatchInfo.getTeamObjective()),
                previousMatchInfo.getTimeInSeconds(),
                previousMatchInfo.getMatchId(),
                previousMatchInfo.getBannedChampions(),
                previousMatchInfo.getSummoners()
        );
    }
}
