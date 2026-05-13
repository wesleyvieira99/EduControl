package com.educontrol.repository;

import com.educontrol.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    List<LibraryItem> findAllByOrderByCreatedAtDesc();
    List<LibraryItem> findBySubjectIdOrderByCreatedAtDesc(Long subjectId);
    List<LibraryItem> findByTypeOrderByCreatedAtDesc(LibraryItem.ItemType type);
    List<LibraryItem> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(String title, String content);
    List<LibraryItem> findByTagsContainingIgnoreCaseOrderByCreatedAtDesc(String tag);
}
