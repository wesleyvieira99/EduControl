package com.educontrol.controller;

import com.educontrol.entity.Subject;
import com.educontrol.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Subject>> findAll() {
        return ResponseEntity.ok(subjectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> findById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Subject> create(@Valid @RequestBody Subject subject) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.save(subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable Long id, @Valid @RequestBody Subject subject) {
        return ResponseEntity.ok(subjectService.update(id, subject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
