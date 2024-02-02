package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.ChampionDto;
import com.lol.stats.model.Champion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChampionMapper {

    public List<Champion> mapToChampionListFromChampionDtoList(final List<ChampionDto> championDtoList) {
        return championDtoList.stream()
                .map(this::mapToChampionFromChampionDto)
                .collect(Collectors.toList());
    }

    public Champion mapToChampionFromChampionDto(final ChampionDto championDto) {
        return Champion.builder()
                .championId(championDto.getChampionId())
                .championLevel(championDto.getChampionLevel())
                .build();
    }
}
