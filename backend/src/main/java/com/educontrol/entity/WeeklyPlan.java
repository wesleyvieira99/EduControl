package com.educontrol.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TopicItem topicItem;

    // 0=Domingo, 1=Segunda, 2=Terça, 3=Quarta, 4=Quinta, 5=Sexta, 6=Sábado
    @Column(nullable = false)
    private Integer dayOfWeek;

    @Builder.Default
    private Integer plannedMinutes = 30;

    @Builder.Default
    private Integer orderIndex = 0;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
