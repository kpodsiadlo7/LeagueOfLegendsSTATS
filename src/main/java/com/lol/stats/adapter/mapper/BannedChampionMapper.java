package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.BannedChampionDto;
import com.lol.stats.model.BannedChampion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannedChampionMapper {
    List<BannedChampionDto> toDtoList(final List<BannedChampion> bannedChampions) {
        return bannedChampions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    BannedChampionDto toDto(final BannedChampion bannedChampion) {
        return new BannedChampionDto(
                bannedChampion.getName()
        );
    }
}
