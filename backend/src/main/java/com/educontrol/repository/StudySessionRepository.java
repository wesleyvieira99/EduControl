package com.educontrol.repository;

import com.educontrol.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByTopicItemIdOrderByStudyDateDesc(Long topicItemId);

    List<StudySession> findByStudyDateOrderByCreatedAtDesc(LocalDate date);

    List<StudySession> findByStudyDateBetweenOrderByStudyDateAsc(LocalDate from, LocalDate to);

    @Query("SELECT SUM(s.durationSeconds) FROM StudySession s WHERE s.studyDate = :date")
    Long sumDurationByDate(LocalDate date);

    @Query("SELECT SUM(s.durationSeconds) FROM StudySession s WHERE s.studyDate BETWEEN :from AND :to")
    Long sumDurationBetween(LocalDate from, LocalDate to);

    @Query("SELECT s.studyDate, SUM(s.durationSeconds) FROM StudySession s WHERE s.studyDate BETWEEN :from AND :to GROUP BY s.studyDate ORDER BY s.studyDate")
    List<Object[]> getDailyTotals(LocalDate from, LocalDate to);

    @Query("SELECT s FROM StudySession s WHERE s.topicItem.topic.subject.id = :subjectId ORDER BY s.studyDate DESC")
    List<StudySession> findBySubjectId(Long subjectId);
}
