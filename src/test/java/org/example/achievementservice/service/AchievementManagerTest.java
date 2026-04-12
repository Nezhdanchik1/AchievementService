package org.example.achievementservice.service;

import org.example.achievementservice.dto.UserActionEvent;
import org.example.achievementservice.model.AchievementEventType;
import org.example.achievementservice.repository.AchievementRepository;
import org.example.achievementservice.repository.ProcessedEventRepository;
import org.example.achievementservice.repository.UserAchievementProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementManagerTest {

    @Mock
    private StatService statService;
    @Mock
    private AchievementRepository achievementRepository;
    @Mock
    private UserAchievementProgressRepository progressRepository;
    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private AchievementManager achievementManager;

    @Test
    void handleAction_ShouldCallStatServiceAndRecordActivity() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-123")
                .userId(1L)
                .type(AchievementEventType.ARTICLE_PUBLISHED)
                .build();

        when(processedEventRepository.existsById("event-123")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), any(), eq(100)); // ARTICLE_PUBLISHED = 100 XP
        verify(statService).recordActivity(1L);
        verify(processedEventRepository).save(any());
    }

    @Test
    void handleAction_ReactionReceived_ShouldGrantReputation() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-456")
                .userId(1L)
                .type(AchievementEventType.REACTION_RECEIVED)
                .build();

        when(processedEventRepository.existsById("event-456")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), any(), eq(10)); // REACTION_RECEIVED = 10 XP
        verify(statService).addReputation(eq(1L), eq(10)); // Reputation also 10
        verify(statService).recordActivity(1L);
    }
}
