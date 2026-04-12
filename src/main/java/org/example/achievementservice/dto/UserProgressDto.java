package org.example.achievementservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProgressDto {
    private Long achievementId;
    private String name;
    private String description;
    private String iconUrl;
    private Integer currentCount;
    private Integer targetCount;
    private Boolean isUnlocked;
    private LocalDateTime unlockedAt;
}
