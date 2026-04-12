package org.example.achievementservice.repository;

import org.example.achievementservice.model.UserDirectionStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDirectionStatsRepository extends JpaRepository<UserDirectionStats, Long> {
    Optional<UserDirectionStats> findByUserIdAndDirectionId(Long userId, Long directionId);
}
