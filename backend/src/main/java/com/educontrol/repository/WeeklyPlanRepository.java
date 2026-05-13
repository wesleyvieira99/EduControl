package com.educontrol.repository;

import com.educontrol.entity.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {
    List<WeeklyPlan> findByDayOfWeekOrderByOrderIndexAsc(Integer dayOfWeek);
    List<WeeklyPlan> findByTopicItemId(Long topicItemId);
    void deleteByTopicItemId(Long topicItemId);
    boolean existsByTopicItemIdAndDayOfWeek(Long topicItemId, Integer dayOfWeek);
}
