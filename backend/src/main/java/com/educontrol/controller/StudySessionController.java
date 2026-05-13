package com.educontrol.controller;

import com.educontrol.entity.StudySession;
import com.educontrol.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService sessionService;

    @GetMapping("/by-item")
    public ResponseEntity<List<StudySession>> findByItem(@RequestParam Long topicItemId) {
        return ResponseEntity.ok(sessionService.findByTopicItemId(topicItemId));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<StudySession>> findByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(sessionService.findByDate(date));
    }

    @GetMapping("/range")
    public ResponseEntity<List<StudySession>> findByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(sessionService.findByDateRange(from, to));
    }

    @PostMapping
    public ResponseEntity<StudySession> create(@RequestParam Long topicItemId, @RequestBody StudySession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.save(topicItemId, session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudySession> update(@PathVariable Long id, @RequestBody StudySession session) {
        return ResponseEntity.ok(sessionService.update(id, session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
