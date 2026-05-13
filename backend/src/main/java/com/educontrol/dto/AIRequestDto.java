package com.educontrol.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIRequestDto {
    private String subjectName;
    private String topicName;
    private String topicItemName;
    private String type; // "questions" | "exercises" | "summary" | "flashcards"
    private Integer quantity;
    private String difficulty; // "easy" | "medium" | "hard"
    private String additionalContext;
}
