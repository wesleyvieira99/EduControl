package com.educontrol.controller;

import com.educontrol.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Slf4j
public class SystemController {

    private final BackupService backupService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/restart")
    public ResponseEntity<Map<String, String>> restart() {
        try {
            backupService.exportSnapshot();
        } catch (Exception e) {
            log.warn("Não foi possível salvar snapshot: {}", e.getMessage());
        }
        return ResponseEntity.ok(Map.of("message", "ok"));
    }
}
