package org.example.achievementservice.repository;

import org.example.achievementservice.model.UserAchievementProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserAchievementProgressRepository extends JpaRepository<UserAchievementProgress, Long> {
    Optional<UserAchievementProgress> findByUserIdAndAchievementId(Long userId, Long achievementId);
    List<UserAchievementProgress> findByUserId(Long userId);
}
