package com.educontrol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupSnapshotDto {

    private String timestamp;
    private String version;
    private List<SubjectEntry> subjects;
    private List<TopicEntry> topics;
    private List<TopicItemEntry> topicItems;
    private List<StudySessionEntry> studySessions;
    private List<NoteEntry> notes;
    private List<LibraryItemEntry> libraryItems;
    private List<WeeklyPlanEntry> weeklyPlans;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubjectEntry {
        private Long id;
        private String name;
        private String description;
        private String color;
        private String emoji;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopicEntry {
        private Long id;
        private Long subjectId;
        private String name;
        private String description;
        private String weekDays;
        private Integer orderIndex;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TopicItemEntry {
        private Long id;
        private Long topicId;
        private String name;
        private String description;
        private LocalDateTime lastStudiedAt;
        private Integer studyCount;
        private Long totalStudySeconds;
        private Integer orderIndex;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StudySessionEntry {
        private Long id;
        private Long topicItemId;
        private LocalDate studyDate;
        private Long durationSeconds;
        private String notes;
        private LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NoteEntry {
        private Long id;
        private String title;
        private String content;
        private Long subjectId;
        private Long topicId;
        private Long topicItemId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LibraryItemEntry {
        private Long id;
        private String title;
        private String content;
        private String url;
        private String author;
        private String type;
        private Long subjectId;
        private String tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WeeklyPlanEntry {
        private Long id;
        private Long topicId;
        private Integer dayOfWeek;
        private Integer plannedMinutes;
        private Integer orderIndex;
        private LocalDateTime createdAt;
    }
}
