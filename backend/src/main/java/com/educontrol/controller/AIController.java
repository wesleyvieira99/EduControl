package com.educontrol.controller;

import com.educontrol.dto.AIRequestDto;
import com.educontrol.dto.AIResponseDto;
import com.educontrol.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/generate")
    public ResponseEntity<AIResponseDto> generate(@RequestBody AIRequestDto request) {
        return ResponseEntity.ok(aiService.generate(request));
    }
}
