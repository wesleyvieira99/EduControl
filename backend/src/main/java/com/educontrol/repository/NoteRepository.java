package com.educontrol.repository;

import com.educontrol.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findBySubjectIdOrderByUpdatedAtDesc(Long subjectId);
    List<Note> findByTopicIdOrderByUpdatedAtDesc(Long topicId);
    List<Note> findByTopicItemIdOrderByUpdatedAtDesc(Long topicItemId);
    List<Note> findAllByOrderByUpdatedAtDesc();
    List<Note> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(String title, String content);
}
