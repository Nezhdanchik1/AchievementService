package org.example.achievementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_daily_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "activity_date")
    private LocalDate activityDate;

    @Column(name = "activity_count")
    private Integer activityCount;
}
