package com.lol.stats.adapter;

import com.lol.stats.dto.SummonerDto;
import com.lol.stats.dto.SummonerInfoDto;
import com.lol.stats.model.Summoner;
import com.lol.stats.model.SummonerInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SummonerMapper {

    SummonerInfo fromSummonerInfoDto(SummonerInfoDto summonerInfoDto);
    SummonerDto toSummonerDto(Summoner summoner);
}
