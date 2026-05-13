package com.educontrol.service;

import com.educontrol.entity.Note;
import com.educontrol.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    public List<Note> findAll() {
        return noteRepository.findAllByOrderByUpdatedAtDesc();
    }

    public List<Note> findBySubjectId(Long subjectId) {
        return noteRepository.findBySubjectIdOrderByUpdatedAtDesc(subjectId);
    }

    public List<Note> findByTopicId(Long topicId) {
        return noteRepository.findByTopicIdOrderByUpdatedAtDesc(topicId);
    }

    public List<Note> findByTopicItemId(Long topicItemId) {
        return noteRepository.findByTopicItemIdOrderByUpdatedAtDesc(topicItemId);
    }

    public List<Note> search(String query) {
        return noteRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByUpdatedAtDesc(query, query);
    }

    public Note findById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anotação não encontrada: " + id));
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public Note update(Long id, Note data) {
        Note existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setContent(data.getContent());
        existing.setSubjectId(data.getSubjectId());
        existing.setTopicId(data.getTopicId());
        existing.setTopicItemId(data.getTopicItemId());
        return noteRepository.save(existing);
    }

    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Anotação não encontrada: " + id);
        }
        noteRepository.deleteById(id);
    }
}
