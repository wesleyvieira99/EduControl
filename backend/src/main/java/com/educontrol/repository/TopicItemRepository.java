package com.educontrol.repository;

import com.educontrol.entity.TopicItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicItemRepository extends JpaRepository<TopicItem, Long> {
    List<TopicItem> findByTopicIdOrderByOrderIndexAscCreatedAtAsc(Long topicId);
    List<TopicItem> findByTopicId(Long topicId);

    @Query("SELECT ti FROM TopicItem ti WHERE ti.topic.subject.id = :subjectId ORDER BY ti.lastStudiedAt DESC NULLS LAST")
    List<TopicItem> findBySubjectIdOrderByLastStudiedAtDesc(Long subjectId);
}
