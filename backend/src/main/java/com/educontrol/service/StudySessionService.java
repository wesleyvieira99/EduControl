package com.educontrol.service;

import com.educontrol.entity.StudySession;
import com.educontrol.entity.TopicItem;
import com.educontrol.repository.StudySessionRepository;
import com.educontrol.repository.TopicItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudySessionService {

    private final StudySessionRepository sessionRepository;
    private final TopicItemRepository topicItemRepository;

    public List<StudySession> findByTopicItemId(Long topicItemId) {
        return sessionRepository.findByTopicItemIdOrderByStudyDateDesc(topicItemId);
    }

    public List<StudySession> findByDate(LocalDate date) {
        return sessionRepository.findByStudyDateOrderByCreatedAtDesc(date);
    }

    public List<StudySession> findByDateRange(LocalDate from, LocalDate to) {
        return sessionRepository.findByStudyDateBetweenOrderByStudyDateAsc(from, to);
    }

    public StudySession save(Long topicItemId, StudySession session) {
        TopicItem item = topicItemRepository.findById(topicItemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + topicItemId));

        session.setTopicItem(item);
        if (session.getStudyDate() == null) session.setStudyDate(LocalDate.now());

        StudySession saved = sessionRepository.save(session);

        // Atualiza estatísticas do TopicItem
        item.setLastStudiedAt(saved.getCreatedAt());
        item.setStudyCount(item.getStudyCount() + 1);
        item.setTotalStudySeconds(item.getTotalStudySeconds() + saved.getDurationSeconds());
        topicItemRepository.save(item);

        return saved;
    }

    public StudySession update(Long id, StudySession data) {
        StudySession existing = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada: " + id));
        existing.setDurationSeconds(data.getDurationSeconds());
        existing.setNotes(data.getNotes());
        existing.setStudyDate(data.getStudyDate());
        return sessionRepository.save(existing);
    }

    public void delete(Long id) {
        StudySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada: " + id));

        // Atualiza estatísticas do item
        TopicItem item = session.getTopicItem();
        item.setStudyCount(Math.max(0, item.getStudyCount() - 1));
        item.setTotalStudySeconds(Math.max(0, item.getTotalStudySeconds() - session.getDurationSeconds()));
        topicItemRepository.save(item);

        sessionRepository.deleteById(id);
    }
}
