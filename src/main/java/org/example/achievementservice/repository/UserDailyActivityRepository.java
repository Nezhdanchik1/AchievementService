package org.example.achievementservice.repository;

import org.example.achievementservice.model.UserDailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDailyActivityRepository extends JpaRepository<UserDailyActivity, Long> {
    Optional<UserDailyActivity> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);
    List<UserDailyActivity> findByUserIdAndActivityDateAfter(Long userId, LocalDate activityDate);
}
