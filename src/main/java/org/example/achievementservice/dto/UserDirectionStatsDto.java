package org.example.achievementservice.dto;

import lombok.*;

@Data
@Builder
public class UserDirectionStatsDto {
    private Long directionId;
    private Long xp;
    private Integer level;
}
