package com.educontrol.service;

import com.educontrol.dto.WeeklyPlanDto;
import com.educontrol.entity.Subject;
import com.educontrol.entity.Topic;
import com.educontrol.entity.WeeklyPlan;
import com.educontrol.repository.TopicRepository;
import com.educontrol.repository.WeeklyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WeeklyPlanService {

    private final WeeklyPlanRepository weeklyPlanRepository;
    private final TopicRepository topicRepository;

    public List<WeeklyPlanDto> findByDay(Integer dayOfWeek) {
        return weeklyPlanRepository.findByDayOfWeekOrderByOrderIndexAsc(dayOfWeek)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<WeeklyPlanDto> findByTopicId(Long topicId) {
        return weeklyPlanRepository.findByTopicId(topicId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public WeeklyPlan save(Long topicId, WeeklyPlan plan) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Tema não encontrado: " + topicId));
        plan.setTopic(topic);
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

    private WeeklyPlanDto toDto(WeeklyPlan plan) {
        Topic topic = plan.getTopic();
        Subject subject = topic != null ? topic.getSubject() : null;
        return WeeklyPlanDto.builder()
                .id(plan.getId())
                .dayOfWeek(plan.getDayOfWeek())
                .plannedMinutes(plan.getPlannedMinutes())
                .orderIndex(plan.getOrderIndex())
                .createdAt(plan.getCreatedAt())
                .topicId(topic != null ? topic.getId() : null)
                .topicName(topic != null ? topic.getName() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectName(subject != null ? subject.getName() : null)
                .subjectColor(subject != null ? subject.getColor() : null)
                .subjectEmoji(subject != null ? subject.getEmoji() : null)
                .build();
    }
}
