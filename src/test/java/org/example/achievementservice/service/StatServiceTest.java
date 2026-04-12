package org.example.achievementservice.service;

import org.example.achievementservice.model.UserDailyActivity;
import org.example.achievementservice.model.UserStats;
import org.example.achievementservice.repository.UserDailyActivityRepository;
import org.example.achievementservice.repository.UserDirectionStatsRepository;
import org.example.achievementservice.repository.UserStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatServiceTest {

    @Mock
    private UserStatsRepository userStatsRepository;
    @Mock
    private UserDirectionStatsRepository directionStatsRepository;
    @Mock
    private UserDailyActivityRepository activityRepository;

    private StatService statService;

    @BeforeEach
    void setUp() {
        statService = new StatService(userStatsRepository, directionStatsRepository, activityRepository);
    }

    @Test
    void addExperience_ShouldUpdateLevelAndXp() {
        Long userId = 1L;
        UserStats stats = UserStats.builder().userId(userId).totalXp(490L).level(1).build();
        when(userStatsRepository.findById(userId)).thenReturn(Optional.of(stats));

        statService.addExperience(userId, null, 20);

        assertEquals(510L, stats.getTotalXp());
        assertEquals(2, stats.getLevel());
        verify(userStatsRepository).save(stats);
    }

    @Test
    void addReputation_ShouldUpdateReputation() {
        Long userId = 1L;
        UserStats stats = UserStats.builder().userId(userId).reputation(100L).build();
        when(userStatsRepository.findById(userId)).thenReturn(Optional.of(stats));

        statService.addReputation(userId, 50);

        assertEquals(150L, stats.getReputation());
        verify(userStatsRepository).save(stats);
    }

    @Test
    void recordActivity_ShouldIncrementCount() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        UserDailyActivity activity = UserDailyActivity.builder()
                .userId(userId)
                .activityDate(today)
                .activityCount(5)
                .build();
        
        when(activityRepository.findByUserIdAndActivityDate(userId, today))
                .thenReturn(Optional.of(activity));

        statService.recordActivity(userId);

        assertEquals(6, activity.getActivityCount());
        verify(activityRepository).save(activity);
    }

    @Test
    void recordActivity_ShouldCreateNewIfNotFound() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        when(activityRepository.findByUserIdAndActivityDate(any(), any()))
                .thenReturn(Optional.empty());

        statService.recordActivity(userId);

        ArgumentCaptor<UserDailyActivity> captor = ArgumentCaptor.forClass(UserDailyActivity.class);
        verify(activityRepository).save(captor.capture());
        
        UserDailyActivity saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(today, saved.getActivityDate());
        assertEquals(1, saved.getActivityCount());
    }
}
