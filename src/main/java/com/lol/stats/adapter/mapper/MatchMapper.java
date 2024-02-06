package com.lol.stats.adapter.mapper;

import com.lol.stats.dto.BannedChampionDto;
import com.lol.stats.dto.MatchDto;
import com.lol.stats.dto.MatchInfoDto;
import com.lol.stats.dto.MatchSummonerDto;
import com.lol.stats.model.BannedChampion;
import com.lol.stats.model.Match;
import com.lol.stats.model.MatchInfo;
import com.lol.stats.model.MatchSummoner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchMapper {

    private final LeagueMapper leagueMapper;
    private final ChampMatchMapper champMatchMapper;

    public MatchDto toDto(final Match match) {
        return new MatchDto(
                match.getId(),
                match.getAccountId(),
                match.getPuuid(),
                match.getName(),
                match.getSummonerLevel(),
                leagueMapper.toDto(match.getLeagueInfo()),
                match.getRank(),
                match.getRankColor(),
                match.getWins(),
                match.getLosses(),
                champMatchMapper.toDtoList(match.getMatches())
        );
    }

    public MatchInfoDto mapToMatchInfoDtoFromMatchInfo(final MatchInfo matchInfo) {
        return new MatchInfoDto(
                matchInfo.getUserTeam(),
                matchInfo.getGameMode(),
                mapToBannedChampionDtoListFromBannedChampionList(matchInfo.getBannedChampions()),
                mapToMatchSummonerDtoListFromMatchSummonerList(matchInfo.getSummoners())
        );
    }

    private List<MatchSummonerDto> mapToMatchSummonerDtoListFromMatchSummonerList(final List<MatchSummoner> summoners) {
        return summoners.stream()
                .map(this::mapToMatchSummonerDtoFromMatchSummoner)
                .collect(Collectors.toList());
    }

    private MatchSummonerDto mapToMatchSummonerDtoFromMatchSummoner(final MatchSummoner matchSummoner) {
        return new MatchSummonerDto(
                matchSummoner.getPuuid(),
                matchSummoner.getTeamId(),
                matchSummoner.getChampionId(),
                matchSummoner.getSummonerName(),
                matchSummoner.getSummonerId(),
                matchSummoner.getRank(),
                matchSummoner.getRankColor(),
                matchSummoner.getChampName(),
                matchSummoner.getSpellName1(),
                matchSummoner.getSpellName2()
        );
    }

    private List<BannedChampionDto> mapToBannedChampionDtoListFromBannedChampionList(List<BannedChampion> bannedChampions) {
        return bannedChampions.stream()
                .map(this::mapToBannedChampionDtoFromBannedChampion)
                .collect(Collectors.toList());
    }

    private BannedChampionDto mapToBannedChampionDtoFromBannedChampion(final BannedChampion bannedChampion) {
        return new BannedChampionDto(
                bannedChampion.getName()
        );
    }
}
