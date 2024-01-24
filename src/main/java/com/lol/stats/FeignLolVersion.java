package com.lol.stats;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "LolVersion", url = "https://ddragon.leagueoflegends.com/api/versions.json")
interface FeignLolVersion {

    @GetMapping
    JsonNode getLolVersions();
}
