package org.example.achievementservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_xp")
    @Builder.Default
    private Long totalXp = 0L;

    @Column(name = "level")
    @Builder.Default
    private Integer level = 1;

    @Column(name = "reputation")
    @Builder.Default
    private Long reputation = 0L;
}
