package com.lol.stats.adapter;

import com.lol.stats.dto.MatchDto;
import com.lol.stats.model.Match;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    MatchDto toDto(Match match);
}
