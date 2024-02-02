package com.lol.stats.domain;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "LolVersion", url = "https://ddragon.leagueoflegends.com/api/versions.json")
public interface ClientLoLVersion {

    @GetMapping
    String[] getLolVersions();
}
