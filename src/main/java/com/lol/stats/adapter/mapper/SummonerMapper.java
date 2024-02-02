package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.SummonerDto;
import com.lol.stats.dto.SummonerInfoDto;
import com.lol.stats.model.Summoner;
import com.lol.stats.model.SummonerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerMapper {

    private final RankMapper rankMapper;

    public SummonerInfo fromSummonerInfoDto(final SummonerInfoDto summonerInfoDto) {
        return SummonerInfo.builder()
                .id(summonerInfoDto.getId())
                .accountId(summonerInfoDto.getAccountId())
                .puuid(summonerInfoDto.getPuuid())
                .name(summonerInfoDto.getName())
                .profileIconId(summonerInfoDto.getProfileIconId())
                .summonerLevel(summonerInfoDto.getSummonerLevel()).build();
    }

    public SummonerDto mapToSummonerDtoFromSummoner(final Summoner summoner) {
        return new SummonerDto(
                summoner.getId(),
                summoner.getAccountId(),
                summoner.getPuuid(),
                summoner.getName(),
                summoner.getProfileIconId(),
                summoner.getSummonerLevel(),
                rankMapper.mapToRankDtoListFromRankList(summoner.getRanks()),
                summoner.getMainChamp(),
                summoner.getRankFlexColor(),
                summoner.getRankSoloColor(),
                summoner.getVersionLoL()
        );
    }
}
