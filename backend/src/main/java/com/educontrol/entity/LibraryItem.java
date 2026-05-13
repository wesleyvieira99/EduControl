package com.educontrol.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "library_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryItem {

    public enum ItemType {
        ARTICLE, BOOK, EXCERPT, INFO, LINK, VIDEO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String url;

    @Column
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ItemType type = ItemType.ARTICLE;

    // Referência à matéria (opcional)
    @Column
    private Long subjectId;

    // Tags separadas por vírgula
    @Column
    private String tags;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
