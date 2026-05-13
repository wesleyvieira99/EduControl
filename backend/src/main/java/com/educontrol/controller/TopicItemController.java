package com.educontrol.controller;

import com.educontrol.entity.TopicItem;
import com.educontrol.service.TopicItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topic-items")
@RequiredArgsConstructor
public class TopicItemController {

    private final TopicItemService topicItemService;

    @GetMapping
    public ResponseEntity<List<TopicItem>> findByTopic(@RequestParam Long topicId) {
        return ResponseEntity.ok(topicItemService.findByTopicId(topicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicItem> findById(@PathVariable Long id) {
        return ResponseEntity.ok(topicItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TopicItem> create(@RequestParam Long topicId, @Valid @RequestBody TopicItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicItemService.save(topicId, item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicItem> update(@PathVariable Long id, @Valid @RequestBody TopicItem item) {
        return ResponseEntity.ok(topicItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
