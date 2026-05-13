package com.educontrol.controller;

import com.educontrol.entity.LibraryItem;
import com.educontrol.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<LibraryItem>> findAll(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) return ResponseEntity.ok(libraryService.search(search));
        if (subjectId != null) return ResponseEntity.ok(libraryService.findBySubjectId(subjectId));
        if (type != null) return ResponseEntity.ok(libraryService.findByType(type));
        return ResponseEntity.ok(libraryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryItem> findById(@PathVariable Long id) {
        return ResponseEntity.ok(libraryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LibraryItem> create(@Valid @RequestBody LibraryItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibraryItem> update(@PathVariable Long id, @Valid @RequestBody LibraryItem item) {
        return ResponseEntity.ok(libraryService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        libraryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
