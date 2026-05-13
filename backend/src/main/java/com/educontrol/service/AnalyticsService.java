package com.educontrol.service;

import com.educontrol.dto.CalendarEventDto;
import com.educontrol.dto.DashboardDto;
import com.educontrol.entity.StudySession;
import com.educontrol.entity.Subject;
import com.educontrol.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final StudySessionRepository sessionRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final TopicItemRepository topicItemRepository;

    public DashboardDto getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);

        Long todaySeconds = Optional.ofNullable(sessionRepository.sumDurationByDate(today)).orElse(0L);
        Long weekSeconds = Optional.ofNullable(sessionRepository.sumDurationBetween(weekStart, today)).orElse(0L);
        Long monthSeconds = Optional.ofNullable(sessionRepository.sumDurationBetween(monthStart, today)).orElse(0L);

        List<StudySession> todaySessions = sessionRepository.findByStudyDateOrderByCreatedAtDesc(today);

        // Últimos 7 dias
        LocalDate sevenDaysAgo = today.minusDays(6);
        List<Object[]> dailyRaw = sessionRepository.getDailyTotals(sevenDaysAgo, today);
        Map<LocalDate, Long> dailyMap = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) dailyMap.put(today.minusDays(i), 0L);
        dailyRaw.forEach(row -> dailyMap.put((LocalDate) row[0], (Long) row[1]));

        List<DashboardDto.DailyStatDto> weeklyStats = dailyMap.entrySet().stream()
                .map(e -> DashboardDto.DailyStatDto.builder()
                        .date(e.getKey())
                        .durationSeconds(e.getValue())
                        .build())
                .collect(Collectors.toList());

        // Estatísticas por matéria (últimos 30 dias)
        List<DashboardDto.SubjectStatDto> subjectStats = new ArrayList<>();
        List<Subject> subjects = subjectRepository.findAll();
        for (Subject s : subjects) {
            List<StudySession> subjectSessions = sessionRepository.findBySubjectId(s.getId());
            long totalSecs = subjectSessions.stream().mapToLong(StudySession::getDurationSeconds).sum();
            if (totalSecs > 0) {
                subjectStats.add(DashboardDto.SubjectStatDto.builder()
                        .subjectId(s.getId())
                        .subjectName(s.getName())
                        .color(s.getColor())
                        .totalSeconds(totalSecs)
                        .sessionsCount(subjectSessions.size())
                        .build());
            }
        }

        // Atividade recente (últimas 10 sessões)
        List<StudySession> recent = sessionRepository.findByStudyDateBetweenOrderByStudyDateAsc(today.minusDays(30), today);
        List<DashboardDto.RecentActivityDto> recentActivity = recent.stream()
                .sorted(Comparator.comparing(StudySession::getCreatedAt).reversed())
                .limit(10)
                .map(s -> DashboardDto.RecentActivityDto.builder()
                        .sessionId(s.getId())
                        .topicItemName(s.getTopicItem().getName())
                        .topicName(s.getTopicItem().getTopic().getName())
                        .subjectName(s.getTopicItem().getTopic().getSubject().getName())
                        .subjectColor(s.getTopicItem().getTopic().getSubject().getColor())
                        .studyDate(s.getStudyDate())
                        .durationSeconds(s.getDurationSeconds())
                        .build())
                .collect(Collectors.toList());

        return DashboardDto.builder()
                .totalStudySecondsToday(todaySeconds)
                .totalStudySecondsWeek(weekSeconds)
                .totalStudySecondsMonth(monthSeconds)
                .totalSessionsToday(todaySessions.size())
                .totalSubjects((int) subjectRepository.count())
                .totalTopics((int) topicRepository.count())
                .totalTopicItems((int) topicItemRepository.count())
                .weeklyStats(weeklyStats)
                .subjectStats(subjectStats)
                .recentActivity(recentActivity)
                .build();
    }

    public List<CalendarEventDto> getCalendarData(LocalDate from, LocalDate to) {
        List<Object[]> dailyRaw = sessionRepository.getDailyTotals(from, to);
        Map<LocalDate, Long> dailyMap = new LinkedHashMap<>();

        // Inicializa todos os dias do range
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            dailyMap.put(cursor, 0L);
            cursor = cursor.plusDays(1);
        }
        dailyRaw.forEach(row -> dailyMap.put((LocalDate) row[0], (Long) row[1]));

        return dailyMap.entrySet().stream()
                .map(e -> {
                    long secs = e.getValue();
                    String level;
                    if (secs == 0) level = "none";
                    else if (secs < 1800) level = "low";
                    else if (secs < 5400) level = "medium";
                    else level = "high";

                    return CalendarEventDto.builder()
                            .date(e.getKey())
                            .totalSeconds(secs)
                            .level(level)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
