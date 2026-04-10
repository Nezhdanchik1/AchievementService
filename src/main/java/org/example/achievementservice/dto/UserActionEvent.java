package org.example.achievementservice.dto;

import lombok.*;
import org.example.achievementservice.model.AchievementEventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionEvent {
    private String eventId;
    private Long userId;
    private AchievementEventType type;
    private Long targetId;
    private Long directionId;
}
