package com.educontrol.service;

import com.educontrol.entity.Subject;
import com.educontrol.entity.Topic;
import com.educontrol.repository.SubjectRepository;
import com.educontrol.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public List<Topic> findBySubjectId(Long subjectId) {
        return topicRepository.findBySubjectIdOrderByOrderIndexAscCreatedAtAsc(subjectId);
    }

    public Topic findById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema não encontrado: " + id));
    }

    public Topic save(Long subjectId, Topic topic) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada: " + subjectId));
        topic.setSubject(subject);
        return topicRepository.save(topic);
    }

    public Topic update(Long id, Topic data) {
        Topic existing = findById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        existing.setWeekDays(data.getWeekDays());
        existing.setOrderIndex(data.getOrderIndex());
        return topicRepository.save(existing);
    }

    public void delete(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new RuntimeException("Tema não encontrado: " + id);
        }
        topicRepository.deleteById(id);
    }
}
