package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordPreviousMatchInfo;
import com.lol.stats.model.PreviousMatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreviousMatchInfoMapper {

    public RecordPreviousMatchInfo toDto(final PreviousMatchInfo previousMatchInfo) {
        return new RecordPreviousMatchInfo(
                previousMatchInfo.getTeamObjective(),
                previousMatchInfo.getTimeInSeconds(),
                previousMatchInfo.getMatchId(),
                previousMatchInfo.getBannedChampions(),
                previousMatchInfo.getSummoners()
        );
    }
}
