package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.SummonerInfoDto;
import com.lol.stats.model.SummonerInfo;
import org.springframework.stereotype.Service;

@Service
public class SummonerInfoMapper {
    public SummonerInfo fromDto(final SummonerInfoDto summonerInfoDto) {
        return SummonerInfo.builder()
                .id(summonerInfoDto.getId())
                .accountId(summonerInfoDto.getAccountId())
                .puuid(summonerInfoDto.getPuuid())
                .tagLine(summonerInfoDto.getTagLine())
                .name(summonerInfoDto.getName())
                .gameName(summonerInfoDto.getGameName())
                .profileIconId(summonerInfoDto.getProfileIconId())
                .summonerLevel(summonerInfoDto.getSummonerLevel()).build();
    }
}
