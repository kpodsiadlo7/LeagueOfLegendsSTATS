package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.RecordSummoner;
import com.lol.stats.model.Summoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummonerMapper {

    private final RankMapper rankMapper;

    public RecordSummoner toDto(final Summoner summoner) {
        return new RecordSummoner(
                summoner.getId(),
                summoner.getAccountId(),
                summoner.getPuuid(),
                summoner.getName(),
                summoner.getProfileIconId(),
                summoner.getSummonerLevel(),
                rankMapper.toDtoList(summoner.getRanks()),
                summoner.getMainChamp(),
                summoner.getRankFlexColor(),
                summoner.getRankSoloColor(),
                summoner.getVersionLoL()
        );
    }
}
