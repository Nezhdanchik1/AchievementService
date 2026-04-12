package org.example.achievementservice.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
public class UserStatsDto {
    private Long userId;
    private Long totalXp;
    private Integer level;
    private Long reputation;
    private List<UserDirectionStatsDto> directionStats;
}
