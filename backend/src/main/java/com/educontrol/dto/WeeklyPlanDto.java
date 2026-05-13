package com.educontrol.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyPlanDto {

    private Long id;
    private Integer dayOfWeek;
    private Integer plannedMinutes;
    private Integer orderIndex;
    private LocalDateTime createdAt;

    // Contexto flat para o frontend
    private Long topicItemId;
    private String topicItemName;
    private Long topicId;
    private String topicName;
    private Long subjectId;
    private String subjectName;
    private String subjectColor;
    private String subjectEmoji;
}
