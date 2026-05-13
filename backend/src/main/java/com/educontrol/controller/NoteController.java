package com.educontrol.controller;

import com.educontrol.entity.Note;
import com.educontrol.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<Note>> findAll(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long topicItemId,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) return ResponseEntity.ok(noteService.search(search));
        if (subjectId != null) return ResponseEntity.ok(noteService.findBySubjectId(subjectId));
        if (topicId != null) return ResponseEntity.ok(noteService.findByTopicId(topicId));
        if (topicItemId != null) return ResponseEntity.ok(noteService.findByTopicItemId(topicItemId));
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Note> create(@Valid @RequestBody Note note) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.save(note));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> update(@PathVariable Long id, @Valid @RequestBody Note note) {
        return ResponseEntity.ok(noteService.update(id, note));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
