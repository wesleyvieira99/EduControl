package com.educontrol.controller;

import com.educontrol.dto.WeeklyPlanDto;
import com.educontrol.entity.WeeklyPlan;
import com.educontrol.service.WeeklyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/weekly-plans")
@RequiredArgsConstructor
public class WeeklyPlanController {

    private final WeeklyPlanService weeklyPlanService;

    @GetMapping
    public ResponseEntity<List<WeeklyPlanDto>> findByDay(@RequestParam Integer dayOfWeek) {
        return ResponseEntity.ok(weeklyPlanService.findByDay(dayOfWeek));
    }

    @GetMapping("/by-topic")
    public ResponseEntity<List<WeeklyPlanDto>> findByTopic(@RequestParam Long topicId) {
        return ResponseEntity.ok(weeklyPlanService.findByTopicId(topicId));
    }

    @PostMapping
    public ResponseEntity<WeeklyPlan> create(@RequestParam Long topicId, @RequestBody WeeklyPlan plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(weeklyPlanService.save(topicId, plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeeklyPlan> update(@PathVariable Long id, @RequestBody WeeklyPlan plan) {
        return ResponseEntity.ok(weeklyPlanService.update(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        weeklyPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
