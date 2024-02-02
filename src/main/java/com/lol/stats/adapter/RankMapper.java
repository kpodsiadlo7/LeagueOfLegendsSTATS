package com.lol.stats.adapter;

import com.lol.stats.dto.RankDto;
import com.lol.stats.model.Rank;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RankMapper {
    List<Rank> fromListRankDto(List<RankDto> rankDto);
    Rank fromDto(RankDto rankDto);
}
