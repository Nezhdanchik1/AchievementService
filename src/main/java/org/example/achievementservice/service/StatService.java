package org.example.achievementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.achievementservice.model.UserDailyActivity;
import org.example.achievementservice.model.UserDirectionStats;
import org.example.achievementservice.model.UserStats;
import org.example.achievementservice.repository.UserDailyActivityRepository;
import org.example.achievementservice.repository.UserDirectionStatsRepository;
import org.example.achievementservice.repository.UserStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatService {

    private final UserStatsRepository userStatsRepository;
    private final UserDirectionStatsRepository directionStatsRepository;
    private final UserDailyActivityRepository activityRepository;

    private static final int XP_PER_LEVEL = 500; // Простая формула для начала

    @Transactional
    public void addExperience(Long userId, Long directionId, int xpAmount) {
        // 1. Глобальный опыт
        UserStats globalStats = userStatsRepository.findById(userId)
                .orElseGet(() -> UserStats.builder().userId(userId).build());
        
        globalStats.setTotalXp(globalStats.getTotalXp() + xpAmount);
        globalStats.setLevel((int) (globalStats.getTotalXp() / XP_PER_LEVEL) + 1);
        userStatsRepository.save(globalStats);

        // 2. Опыт по направлению
        if (directionId != null) {
            UserDirectionStats dirStats = directionStatsRepository.findByUserIdAndDirectionId(userId, directionId)
                    .orElseGet(() -> UserDirectionStats.builder()
                            .userId(userId)
                            .directionId(directionId)
                            .build());
            
            dirStats.setXp(dirStats.getXp() + xpAmount);
            dirStats.setLevel((int) (dirStats.getXp() / XP_PER_LEVEL) + 1);
            directionStatsRepository.save(dirStats);
            log.info("User {} gained {} XP in direction {}. New level: {}", userId, xpAmount, directionId, dirStats.getLevel());
        }
    }

    @Transactional
    public void addReputation(Long userId, int reputationAmount) {
        UserStats globalStats = userStatsRepository.findById(userId)
                .orElseGet(() -> UserStats.builder().userId(userId).build());

        globalStats.setReputation(globalStats.getReputation() + reputationAmount);
        userStatsRepository.save(globalStats);
        log.info("User {} gained {} reputation. Total: {}", userId, reputationAmount, globalStats.getReputation());
    }

    @Transactional
    public void recordActivity(Long userId) {
        LocalDate today = LocalDate.now();
        UserDailyActivity activity = activityRepository.findByUserIdAndActivityDate(userId, today)
                .orElseGet(() -> UserDailyActivity.builder()
                        .userId(userId)
                        .activityDate(today)
                        .activityCount(0)
                        .build());

        activity.setActivityCount(activity.getActivityCount() + 1);
        activityRepository.save(activity);
    }
}
