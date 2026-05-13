package com.educontrol.service;

import com.educontrol.entity.LibraryItem;
import com.educontrol.repository.LibraryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibraryService {

    private final LibraryItemRepository libraryItemRepository;

    public List<LibraryItem> findAll() {
        return libraryItemRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<LibraryItem> findBySubjectId(Long subjectId) {
        return libraryItemRepository.findBySubjectIdOrderByCreatedAtDesc(subjectId);
    }

    public List<LibraryItem> findByType(String type) {
        return libraryItemRepository.findByTypeOrderByCreatedAtDesc(LibraryItem.ItemType.valueOf(type.toUpperCase()));
    }

    public List<LibraryItem> search(String query) {
        return libraryItemRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(query, query);
    }

    public LibraryItem findById(Long id) {
        return libraryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item da biblioteca não encontrado: " + id));
    }

    public LibraryItem save(LibraryItem item) {
        return libraryItemRepository.save(item);
    }

    public LibraryItem update(Long id, LibraryItem data) {
        LibraryItem existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setContent(data.getContent());
        existing.setUrl(data.getUrl());
        existing.setAuthor(data.getAuthor());
        existing.setType(data.getType());
        existing.setSubjectId(data.getSubjectId());
        existing.setTags(data.getTags());
        return libraryItemRepository.save(existing);
    }

    public void delete(Long id) {
        if (!libraryItemRepository.existsById(id)) {
            throw new RuntimeException("Item da biblioteca não encontrado: " + id);
        }
        libraryItemRepository.deleteById(id);
    }
}
