package com.educontrol.controller;

import com.educontrol.entity.Topic;
import com.educontrol.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<List<Topic>> findBySubject(@RequestParam Long subjectId) {
        return ResponseEntity.ok(topicService.findBySubjectId(subjectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> findById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Topic> create(@RequestParam Long subjectId, @Valid @RequestBody Topic topic) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.save(subjectId, topic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> update(@PathVariable Long id, @Valid @RequestBody Topic topic) {
        return ResponseEntity.ok(topicService.update(id, topic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
