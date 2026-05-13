package com.educontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseDto {
    private String content;
    private boolean success;
    private String error;

    public static AIResponseDto success(String content) {
        return new AIResponseDto(content, true, null);
    }

    public static AIResponseDto error(String error) {
        return new AIResponseDto(null, false, error);
    }
}
