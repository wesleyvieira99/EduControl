package com.educontrol.service;

import com.educontrol.entity.Subject;
import com.educontrol.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> findAll() {
        return subjectRepository.findAllByOrderByCreatedAtAsc();
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada: " + id));
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject update(Long id, Subject data) {
        Subject existing = findById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        existing.setColor(data.getColor());
        existing.setEmoji(data.getEmoji());
        return subjectRepository.save(existing);
    }

    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Matéria não encontrada: " + id);
        }
        subjectRepository.deleteById(id);
    }
}
