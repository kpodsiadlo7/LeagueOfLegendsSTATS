package com.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamObjectiveDto {
    private String championKills;
    private String baronKills;
    private String dragonKills;
    private int teamId;
}
