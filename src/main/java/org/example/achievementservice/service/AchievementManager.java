package org.example.achievementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.achievementservice.dto.UserActionEvent;
import org.example.achievementservice.model.*;
import org.example.achievementservice.repository.AchievementRepository;
import org.example.achievementservice.repository.ProcessedEventRepository;
import org.example.achievementservice.repository.UserAchievementProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementManager {

    private final StatService statService;
    private final AchievementRepository achievementRepository;
    private final UserAchievementProgressRepository progressRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void handleAction(UserActionEvent event) {
        // 1. Идемпотентность
        if (processedEventRepository.existsById(event.getEventId())) {
            log.warn("Event {} already processed. Skipping.", event.getEventId());
            return;
        }

        log.info("Processing action {} for user {}", event.getType(), event.getUserId());

        // 2. Начисление XP
        int xpAmount = calculateXp(event.getType());
        statService.addExperience(event.getUserId(), event.getDirectionId(), xpAmount);

        // 3. Прогресс ачивок
        updateAchievements(event);

        // 4. Помечаем событие как обработанное
        processedEventRepository.save(new ProcessedEvent(event.getEventId(), LocalDateTime.now()));
    }

    private void updateAchievements(UserActionEvent event) {
        // Ищем ачивки: либо глобальные (directionId is null), либо для этого направления
        List<Achievement> potentialAchievements = achievementRepository.findByEventType(event.getType());

        for (Achievement ach : potentialAchievements) {
            // Если у ачивки есть направление, и оно не совпадает — пропускаем
            if (ach.getDirectionId() != null && !ach.getDirectionId().equals(event.getDirectionId())) {
                continue;
            }

            UserAchievementProgress progress = progressRepository.findByUserIdAndAchievementId(event.getUserId(), ach.getId())
                    .orElseGet(() -> UserAchievementProgress.builder()
                            .userId(event.getUserId())
                            .achievement(ach)
                            .build());

            if (progress.getIsUnlocked()) continue;

            progress.setCurrentCount(progress.getCurrentCount() + 1);
            if (progress.getCurrentCount() >= ach.getTargetCount()) {
                progress.setIsUnlocked(true);
                progress.setUnlockedAt(LocalDateTime.now());
                log.info("🏆 Achievement Unlocked: {} for user {}", ach.getName(), event.getUserId());
                // Здесь в будущем можно отправить событие ACHIEVEMENT_UNLOCKED в RabbitMQ для уведомлений
            }
            progressRepository.save(progress);
        }
    }

    private int calculateXp(AchievementEventType type) {
        return switch (type) {
            case ARTICLE_READ -> 5;
            case COMMENT_ADDED -> 15;
            case ARTICLE_PUBLISHED -> 100;
            case REACTION_RECEIVED -> 10;
            case LOGIN_COMPLETED -> 20;
            default -> 0;
        };
    }
}
