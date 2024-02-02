package com.lol.stats.adapter;

import com.lol.stats.dto.ChampionDto;
import com.lol.stats.model.Champion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChampionMapper {

    List<Champion> fromChampionDtoList(final List<ChampionDto> championDtoList);
    Champion fromDto(ChampionDto championDto);
}
