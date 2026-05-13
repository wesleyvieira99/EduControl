package com.educontrol.controller;

import com.educontrol.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/export")
    public ResponseEntity<Map<String, String>> export() {
        try {
            String filename = backupService.exportSnapshot();
            return ResponseEntity.ok(Map.of(
                    "filename", filename,
                    "message", "Snapshot exportado com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, String>> importLatest() {
        try {
            String filename = backupService.importLatestSnapshot();
            return ResponseEntity.ok(Map.of(
                    "filename", filename,
                    "message", "Dados restaurados com sucesso de: " + filename
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
