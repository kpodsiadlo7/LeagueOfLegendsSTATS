package com.lol.stats.adapter;

import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.model.Match;
import com.lol.stats.model.MatchInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    MatchDto toMatchDto(final Match match);

    MatchInfoDto toMatchInfoDto(final MatchInfo matchInfo);
}
