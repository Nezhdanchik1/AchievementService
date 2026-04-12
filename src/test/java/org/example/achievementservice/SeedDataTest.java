package org.example.achievementservice;

import org.example.achievementservice.model.UserDailyActivity;
import org.example.achievementservice.repository.UserDailyActivityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Random;

@SpringBootTest
public class SeedDataTest {

    @Autowired
    private UserDailyActivityRepository activityRepository;

    @Test
    @Transactional
    @Commit
    public void seedActivityData() {
        Long userId = 1L;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(180);
        Random random = new Random();

        System.out.println("Starting seeding activity for user " + userId);

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            // 70% chance of activity
            if (random.nextDouble() < 0.7) {
                int count = random.nextInt(12) + 1;
                
                UserDailyActivity activity = activityRepository.findByUserIdAndActivityDate(userId, date)
                        .orElseGet(() -> UserDailyActivity.builder()
                                .userId(userId)
                                .activityDate(date)
                                .build());
                
                activity.setActivityCount(count);
                activityRepository.save(activity);
            }
        }
        
        System.out.println("Seeding completed successfully!");
    }
}
