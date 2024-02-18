package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.SummonerInfoDto;
import com.lol.stats.model.SummonerInfo;
import org.springframework.stereotype.Service;

@Service
public class SummonerInfoMapper {
    public SummonerInfo fromDto(final SummonerInfoDto summonerInfoDto) {
        return SummonerInfo.builder()
                .id(summonerInfoDto.id())
                .accountId(summonerInfoDto.accountId())
                .puuid(summonerInfoDto.puuid())
                .tagLine(summonerInfoDto.tagLine())
                .name(summonerInfoDto.name())
                .gameName(summonerInfoDto.gameName())
                .profileIconId(summonerInfoDto.profileIconId())
                .summonerLevel(summonerInfoDto.summonerLevel()).build();
    }
}
