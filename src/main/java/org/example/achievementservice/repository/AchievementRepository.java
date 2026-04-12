package org.example.achievementservice.repository;

import org.example.achievementservice.model.Achievement;
import org.example.achievementservice.model.AchievementEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByEventType(AchievementEventType eventType);
    List<Achievement> findByEventTypeAndDirectionIdIsNull(AchievementEventType eventType);
    List<Achievement> findByEventTypeAndDirectionId(AchievementEventType eventType, Long directionId);
}
