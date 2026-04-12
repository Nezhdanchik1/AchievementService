package org.example.achievementservice.service;

import org.example.achievementservice.dto.UserActionEvent;
import org.example.achievementservice.model.*;
import org.example.achievementservice.repository.AchievementRepository;
import org.example.achievementservice.repository.ProcessedEventRepository;
import org.example.achievementservice.repository.UserAchievementProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void handleAction_ArticlePublished_ShouldGrant100Xp() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-1")
                .userId(1L)
                .type(AchievementEventType.ARTICLE_PUBLISHED)
                .build();

        when(processedEventRepository.existsById("event-1")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), any(), eq(100));
        verify(statService).recordActivity(1L);
    }

    @Test
    void handleAction_CommentAdded_ShouldGrant15Xp() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-2")
                .userId(1L)
                .type(AchievementEventType.COMMENT_ADDED)
                .build();

        when(processedEventRepository.existsById("event-2")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), any(), eq(15));
    }

    @Test
    void handleAction_ReactionReceived_ShouldGrantXpAndReputation() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-3")
                .userId(1L)
                .type(AchievementEventType.REACTION_RECEIVED)
                .build();

        when(processedEventRepository.existsById("event-3")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), any(), eq(10));
        verify(statService).addReputation(eq(1L), eq(10));
    }

    @Test
    void handleAction_ArticleRead_ShouldGrant5XpAndTrackDirection() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-4")
                .userId(1L)
                .directionId(2L)
                .type(AchievementEventType.ARTICLE_READ)
                .build();

        when(processedEventRepository.existsById("event-4")).thenReturn(false);
        when(achievementRepository.findByEventType(any())).thenReturn(List.of());

        achievementManager.handleAction(event);

        verify(statService).addExperience(eq(1L), eq(2L), eq(5));
    }

    @Test
    void handleAction_ShouldUnlockGlobalAchievement() {
        Achievement ach = Achievement.builder()
                .id(1L)
                .name("First Steps")
                .targetCount(1)
                .eventType(AchievementEventType.ARTICLE_READ)
                .build();

        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-5")
                .userId(1L)
                .type(AchievementEventType.ARTICLE_READ)
                .build();

        when(processedEventRepository.existsById("event-5")).thenReturn(false);
        when(achievementRepository.findByEventType(AchievementEventType.ARTICLE_READ)).thenReturn(List.of(ach));
        when(progressRepository.findByUserIdAndAchievementId(1L, 1L)).thenReturn(Optional.empty());

        achievementManager.handleAction(event);

        verify(progressRepository).save(argThat(UserAchievementProgress::getIsUnlocked));
    }

    @Test
    void handleAction_ShouldUnlockDirectionalAchievement() {
        Long targetDirectionId = 2L;
        Achievement ach = Achievement.builder()
                .id(2L)
                .name("Backend Pro")
                .targetCount(1)
                .directionId(targetDirectionId)
                .eventType(AchievementEventType.ARTICLE_READ)
                .build();

        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-6")
                .userId(1L)
                .directionId(targetDirectionId)
                .type(AchievementEventType.ARTICLE_READ)
                .build();

        when(processedEventRepository.existsById("event-6")).thenReturn(false);
        when(achievementRepository.findByEventType(AchievementEventType.ARTICLE_READ)).thenReturn(List.of(ach));
        when(progressRepository.findByUserIdAndAchievementId(1L, 2L)).thenReturn(Optional.empty());

        achievementManager.handleAction(event);

        verify(progressRepository).save(argThat(UserAchievementProgress::getIsUnlocked));
    }

    @Test
    void handleAction_ShouldIgnoreDirectionalAchievement_IfDirectionMismatch() {
        Achievement ach = Achievement.builder()
                .id(3L)
                .directionId(1L) // Frontend ach
                .targetCount(1)
                .eventType(AchievementEventType.ARTICLE_READ)
                .build();

        UserActionEvent event = UserActionEvent.builder()
                .eventId("event-7")
                .userId(1L)
                .directionId(2L) // Backend action
                .type(AchievementEventType.ARTICLE_READ)
                .build();

        when(processedEventRepository.existsById("event-7")).thenReturn(false);
        when(achievementRepository.findByEventType(AchievementEventType.ARTICLE_READ)).thenReturn(List.of(ach));

        achievementManager.handleAction(event);

        verify(progressRepository, never()).save(any());
    }

    @Test
    void handleAction_Idempotency_ShouldSkipProcessedEvents() {
        UserActionEvent event = UserActionEvent.builder()
                .eventId("duplicate")
                .userId(1L)
                .type(AchievementEventType.COMMENT_ADDED)
                .build();

        when(processedEventRepository.existsById("duplicate")).thenReturn(true);

        achievementManager.handleAction(event);

        verify(statService, never()).addExperience(any(), any(), anyInt());
        verify(processedEventRepository, never()).save(any());
    }
}
