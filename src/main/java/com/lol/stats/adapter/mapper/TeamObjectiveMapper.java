package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.TeamObjectiveDto;
import com.lol.stats.model.TeamObjective;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamObjectiveMapper {
    public List<TeamObjectiveDto> toDtoList(final List<TeamObjective> teamObjective) {
        return teamObjective.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TeamObjectiveDto toDto(final TeamObjective teamObjective) {
        return new TeamObjectiveDto(
                teamObjective.getChampionKills(),
                teamObjective.getBaronKills(),
                teamObjective.getDragonKills(),
                teamObjective.getTeamId()
        );
    }
}
