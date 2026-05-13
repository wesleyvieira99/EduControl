package com.educontrol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topic_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Topic topic;

    @Column
    private LocalDateTime lastStudiedAt;

    @Builder.Default
    private Integer studyCount = 0;

    @Builder.Default
    private Long totalStudySeconds = 0L;

    @Column
    @Builder.Default
    private Integer orderIndex = 0;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "topicItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudySession> sessions = new ArrayList<>();

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
