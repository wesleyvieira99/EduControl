package com.educontrol.controller;

import com.educontrol.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Slf4j
public class SystemController {

    private final BackupService backupService;

    @Value("${backup.project.root:..}")
    private String projectRoot;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/restart")
    public ResponseEntity<Map<String, String>> restart() {
        // Salva snapshot antes de reiniciar
        try {
            backupService.exportSnapshot();
        } catch (Exception e) {
            log.warn("Não foi possível salvar snapshot antes de reiniciar: {}", e.getMessage());
        }

        // Inicia novo processo em background e mata o atual
        new Thread(() -> {
            try {
                Thread.sleep(500);
                String backendDir = System.getProperty("user.dir");
                String logDir = java.nio.file.Paths.get(projectRoot, "logs").toAbsolutePath().normalize().toString();
                String cmd = String.format(
                    "export PATH='/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:$PATH'; " +
                    "nohup mvn spring-boot:run --no-transfer-progress -f '%s/pom.xml' >> '%s/backend.log' 2>&1 &",
                    backendDir, logDir
                );
                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                pb.redirectError(ProcessBuilder.Redirect.DISCARD);
                pb.start();
                Thread.sleep(300);
                System.exit(0);
            } catch (Exception e) {
                log.error("Falha ao reiniciar", e);
                System.exit(1);
            }
        }).start();

        return ResponseEntity.ok(Map.of("message", "Reiniciando..."));
    }
}
