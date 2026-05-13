package com.educontrol.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_item_id", nullable = false)
    @JsonBackReference("topicitem-sessions")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TopicItem topicItem;

    @Column(nullable = false)
    private LocalDate studyDate;

    @Column(nullable = false)
    @Builder.Default
    private Long durationSeconds = 0L;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (studyDate == null) studyDate = LocalDate.now();
    }
}
