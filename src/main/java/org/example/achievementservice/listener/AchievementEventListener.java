package org.example.achievementservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.achievementservice.config.RabbitConfig;
import org.example.achievementservice.dto.UserActionEvent;
import org.example.achievementservice.service.AchievementManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementEventListener {

    private final AchievementManager achievementManager;

    @RabbitListener(queues = RabbitConfig.QUEUE_ACHIEVEMENTS)
    public void handleUserAction(UserActionEvent event) {
        try {
            achievementManager.handleAction(event);
        } catch (Exception e) {
            log.error("Error processing achievement event: {}", event.getEventId(), e);
            // Здесь можно добавить логику переповтора или отправки в DLQ
        }
    }
}
