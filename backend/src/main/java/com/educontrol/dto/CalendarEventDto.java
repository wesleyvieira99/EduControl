package com.educontrol.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class CalendarEventDto {
    private LocalDate date;
    private Long totalSeconds;
    private Integer sessionsCount;
    private String level; // "none", "low", "medium", "high"
}
