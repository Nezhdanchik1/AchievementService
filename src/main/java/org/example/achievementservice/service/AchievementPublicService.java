package org.example.achievementservice.service;

import lombok.RequiredArgsConstructor;
import org.example.achievementservice.dto.UserActivityDto;
import org.example.achievementservice.dto.UserDirectionStatsDto;
import org.example.achievementservice.dto.UserProgressDto;
import org.example.achievementservice.dto.UserStatsDto;
import org.example.achievementservice.model.Achievement;
import org.example.achievementservice.model.UserAchievementProgress;
import org.example.achievementservice.model.UserStats;
import org.example.achievementservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementPublicService {

    private final UserStatsRepository userStatsRepository;
    private final UserDirectionStatsRepository directionStatsRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementProgressRepository progressRepository;
    private final UserDailyActivityRepository activityRepository;

    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(Long userId) {
        UserStats stats = userStatsRepository.findById(userId)
                .orElseGet(() -> UserStats.builder().userId(userId).build());

        // Здесь можно добавить логику получения directionStats
        // Для простоты пока вернем пустой список или базовый
        return UserStatsDto.builder()
                .userId(userId)
                .totalXp(stats.getTotalXp())
                .level(stats.getLevel())
                .reputation(stats.getReputation())
                .directionStats(List.of()) 
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserProgressDto> getUserAchievements(Long userId) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UserAchievementProgress> userProgress = progressRepository.findByUserId(userId);

        return allAchievements.stream().map(ach -> {
            UserAchievementProgress progress = userProgress.stream()
                    .filter(p -> p.getAchievement().getId().equals(ach.getId()))
                    .findFirst()
                    .orElse(null);

            return UserProgressDto.builder()
                    .achievementId(ach.getId())
                    .name(ach.getName())
                    .description(ach.getDescription())
                    .iconUrl(ach.getIconUrl())
                    .currentCount(progress != null ? progress.getCurrentCount() : 0)
                    .targetCount(ach.getTargetCount())
                    .isUnlocked(progress != null && progress.getIsUnlocked())
                    .unlockedAt(progress != null ? progress.getUnlockedAt() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserActivityDto> getUserActivityGrid(Long userId) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        return activityRepository.findByUserIdAndActivityDateAfter(userId, oneYearAgo).stream()
                .map(activity -> UserActivityDto.builder()
                        .date(activity.getActivityDate())
                        .count(activity.getActivityCount())
                        .build())
                .collect(Collectors.toList());
    }
}
