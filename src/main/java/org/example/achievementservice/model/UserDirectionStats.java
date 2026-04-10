package org.example.achievementservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_direction_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDirectionStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "direction_id", nullable = false)
    private Long directionId;

    @Column(name = "xp")
    @Builder.Default
    private Long xp = 0L;

    @Column(name = "level")
    @Builder.Default
    private Integer level = 1;
}
