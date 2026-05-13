package com.educontrol.service;

import com.educontrol.dto.AIRequestDto;
import com.educontrol.dto.AIResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    public AIResponseDto generate(AIRequestDto request) {
        try {
            String prompt = buildPrompt(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(message));
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> msg = (Map<String, Object>) choice.get("message");
                    String content = (String) msg.get("content");
                    return AIResponseDto.success(content);
                }
            }
            return AIResponseDto.error("Resposta inválida da IA");
        } catch (Exception e) {
            return AIResponseDto.error("Erro ao comunicar com IA: " + e.getMessage());
        }
    }

    private String buildPrompt(AIRequestDto req) {
        int qty = req.getQuantity() != null ? req.getQuantity() : 5;
        String difficulty = req.getDifficulty() != null ? req.getDifficulty() : "medium";
        String context = req.getAdditionalContext() != null ? "\nContexto adicional: " + req.getAdditionalContext() : "";

        String difficultyText = switch (difficulty) {
            case "easy" -> "fácil/básico";
            case "hard" -> "difícil/avançado";
            default -> "médio/intermediário";
        };

        String typeText = switch (Optional.ofNullable(req.getType()).orElse("questions")) {
            case "exercises" -> "exercícios práticos";
            case "summary" -> "um resumo detalhado";
            case "flashcards" -> "flashcards (frente e verso)";
            default -> "questões de revisão";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("Você é um tutor educacional especializado. ");
        sb.append("Gere ").append(qty).append(" ").append(typeText).append(" de nível ").append(difficultyText);
        sb.append(" sobre o seguinte conteúdo:\n");
        if (req.getSubjectName() != null) sb.append("- Matéria: ").append(req.getSubjectName()).append("\n");
        if (req.getTopicName() != null) sb.append("- Tema: ").append(req.getTopicName()).append("\n");
        if (req.getTopicItemName() != null) sb.append("- Conteúdo específico: ").append(req.getTopicItemName()).append("\n");
        sb.append(context);
        sb.append("\nResponda em português brasileiro. Formate a resposta de forma clara e organizada, numerando cada item.");

        return sb.toString();
    }
}
