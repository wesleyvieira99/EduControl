package com.educontrol.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardDto {
    private Long totalStudySecondsToday;
    private Long totalStudySecondsWeek;
    private Long totalStudySecondsMonth;
    private Integer totalSessionsToday;
    private Integer totalSubjects;
    private Integer totalTopics;
    private Integer totalTopicItems;
    private List<DailyStatDto> weeklyStats;
    private List<SubjectStatDto> subjectStats;
    private List<RecentActivityDto> recentActivity;

    @Data
    @Builder
    public static class DailyStatDto {
        private LocalDate date;
        private Long durationSeconds;
        private Integer sessions;
    }

    @Data
    @Builder
    public static class SubjectStatDto {
        private Long subjectId;
        private String subjectName;
        private String color;
        private Long totalSeconds;
        private Integer sessionsCount;
    }

    @Data
    @Builder
    public static class RecentActivityDto {
        private Long sessionId;
        private String topicItemName;
        private String topicName;
        private String subjectName;
        private String subjectColor;
        private LocalDate studyDate;
        private Long durationSeconds;
    }
}
