package org.example.achievementservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.achievementservice.dto.UserActivityDto;
import org.example.achievementservice.dto.UserProgressDto;
import org.example.achievementservice.dto.UserStatsDto;
import org.example.achievementservice.service.AchievementPublicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementPublicService achievementService;

    @GetMapping("/my")
    public ResponseEntity<List<UserProgressDto>> getMyAchievements(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> getMyStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(achievementService.getUserStats(userId));
    }

    @GetMapping("/activity")
    public ResponseEntity<List<UserActivityDto>> getMyActivity(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(achievementService.getUserActivityGrid(userId));
    }

    // Для целей тестирования, если X-User-Id не пробрасывается через Gateway напрямую в заголовках при локальном вызове
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<UserStatsDto> getUserStats(@PathVariable Long userId) {
        return ResponseEntity.ok(achievementService.getUserStats(userId));
    }

    @GetMapping("/user/{userId}/activity")
    public ResponseEntity<List<UserActivityDto>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(achievementService.getUserActivityGrid(userId));
    }
}
