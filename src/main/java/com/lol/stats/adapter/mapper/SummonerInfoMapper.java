package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordSummonerInfo;
import com.lol.stats.model.SummonerInfo;
import org.springframework.stereotype.Service;

@Service
public class SummonerInfoMapper {
    public SummonerInfo fromDto(final RecordSummonerInfo recordSummonerInfo) {
        return SummonerInfo.builder()
                .id(recordSummonerInfo.id())
                .accountId(recordSummonerInfo.accountId())
                .puuid(recordSummonerInfo.puuid())
                .tagLine(recordSummonerInfo.tagLine())
                .name(recordSummonerInfo.name())
                .gameName(recordSummonerInfo.gameName())
                .profileIconId(recordSummonerInfo.profileIconId())
                .summonerLevel(recordSummonerInfo.summonerLevel()).build();
    }
}
