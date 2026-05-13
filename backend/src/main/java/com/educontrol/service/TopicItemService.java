package com.educontrol.service;

import com.educontrol.entity.Topic;
import com.educontrol.entity.TopicItem;
import com.educontrol.repository.TopicItemRepository;
import com.educontrol.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicItemService {

    private final TopicItemRepository topicItemRepository;
    private final TopicRepository topicRepository;

    public List<TopicItem> findByTopicId(Long topicId) {
        return topicItemRepository.findByTopicIdOrderByOrderIndexAscCreatedAtAsc(topicId);
    }

    public TopicItem findById(Long id) {
        return topicItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + id));
    }

    public TopicItem save(Long topicId, TopicItem item) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Tema não encontrado: " + topicId));
        item.setTopic(topic);
        return topicItemRepository.save(item);
    }

    public TopicItem update(Long id, TopicItem data) {
        TopicItem existing = findById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        existing.setOrderIndex(data.getOrderIndex());
        return topicItemRepository.save(existing);
    }

    public void delete(Long id) {
        if (!topicItemRepository.existsById(id)) {
            throw new RuntimeException("Item não encontrado: " + id);
        }
        topicItemRepository.deleteById(id);
    }
}
