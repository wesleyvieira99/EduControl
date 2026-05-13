package com.educontrol.service;

import com.educontrol.entity.TopicItem;
import com.educontrol.entity.WeeklyPlan;
import com.educontrol.repository.TopicItemRepository;
import com.educontrol.repository.WeeklyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyPlanService {

    private final WeeklyPlanRepository weeklyPlanRepository;
    private final TopicItemRepository topicItemRepository;

    public List<WeeklyPlan> findByDay(Integer dayOfWeek) {
        return weeklyPlanRepository.findByDayOfWeekOrderByOrderIndexAsc(dayOfWeek);
    }

    public List<WeeklyPlan> findByTopicItemId(Long topicItemId) {
        return weeklyPlanRepository.findByTopicItemId(topicItemId);
    }

    public WeeklyPlan save(Long topicItemId, WeeklyPlan plan) {
        TopicItem item = topicItemRepository.findById(topicItemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + topicItemId));
        plan.setTopicItem(item);
        return weeklyPlanRepository.save(plan);
    }

    public WeeklyPlan update(Long id, WeeklyPlan data) {
        WeeklyPlan existing = weeklyPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano semanal não encontrado: " + id));
        existing.setPlannedMinutes(data.getPlannedMinutes());
        existing.setOrderIndex(data.getOrderIndex());
        return weeklyPlanRepository.save(existing);
    }

    public void delete(Long id) {
        if (!weeklyPlanRepository.existsById(id)) {
            throw new RuntimeException("Plano semanal não encontrado: " + id);
        }
        weeklyPlanRepository.deleteById(id);
    }
}
