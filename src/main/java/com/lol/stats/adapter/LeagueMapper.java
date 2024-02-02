package com.lol.stats.adapter;

import com.lol.stats.dto.LeagueInfoDto;
import com.lol.stats.model.LeagueInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeagueMapper {
    List<LeagueInfo> fromLeagueInfoDto(List<LeagueInfoDto> leagueInfoDtoList);

    LeagueInfo fromDto(LeagueInfoDto leagueInfoDto);
}
