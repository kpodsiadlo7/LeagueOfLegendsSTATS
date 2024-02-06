package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.PreviousMatchInfoDto;
import com.lol.stats.model.PreviousMatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreviousMatchInfoMapper {

    private final ChampMatchMapper champMatchMapper;
    private final TeamObjectiveMapper teamObjectiveMapper;
    private final MatchMapper matchMapper;

    public PreviousMatchInfoDto toDto(PreviousMatchInfo previousMatchInfo) {
        return new PreviousMatchInfoDto(
                champMatchMapper.toDtoList(previousMatchInfo.getMatchList()),
                teamObjectiveMapper.toDtoList(previousMatchInfo.getTeamObjective()),
                matchMapper.mapToMatchInfoDtoFromMatchInfo(previousMatchInfo.getMatchInfo())
        );
    }
}
