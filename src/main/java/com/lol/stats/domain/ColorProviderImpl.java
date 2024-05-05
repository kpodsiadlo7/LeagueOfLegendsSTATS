package com.lol.stats.domain;

import org.springframework.stereotype.Component;

@Component
class ColorProviderImpl implements ColorProvider {
    @Override
    public String provideColor(String rank) {
        return switch (rank.toLowerCase()) {
            case "gold" -> "#FFD700";
            case "silver" -> "#C0C0C0";
            case "platinum" -> "#A9A9A9";
            case "emerald" -> "#2ecc71";
            case "diamond" -> "#00CED1";
            case "bronze" -> "#964B00";
            case "grandmaster" -> "#3333FF";
            case "master" -> "#800080";
            default -> "#363949";
        };
    }
}
