package com.educontrol.service;

import com.educontrol.dto.BackupSnapshotDto;
import com.educontrol.entity.*;
import com.educontrol.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupService {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final TopicItemRepository topicItemRepository;
    private final StudySessionRepository studySessionRepository;
    private final NoteRepository noteRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final ObjectMapper objectMapper;

    @Value("${backup.history.path:../history}")
    private String historyPath;

    @Value("${backup.project.root:..}")
    private String projectRoot;

    @Transactional(readOnly = true)
    public BackupSnapshotDto buildSnapshot() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<BackupSnapshotDto.SubjectEntry> subjects = subjectRepository.findAll().stream()
                .map(s -> BackupSnapshotDto.SubjectEntry.builder()
                        .id(s.getId()).name(s.getName()).description(s.getDescription())
                        .color(s.getColor()).emoji(s.getEmoji())
                        .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.TopicEntry> topics = topicRepository.findAll().stream()
                .map(t -> BackupSnapshotDto.TopicEntry.builder()
                        .id(t.getId()).subjectId(t.getSubject().getId())
                        .name(t.getName()).description(t.getDescription())
                        .weekDays(t.getWeekDays()).orderIndex(t.getOrderIndex())
                        .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.TopicItemEntry> topicItems = topicItemRepository.findAll().stream()
                .map(ti -> BackupSnapshotDto.TopicItemEntry.builder()
                        .id(ti.getId()).topicId(ti.getTopic().getId())
                        .name(ti.getName()).description(ti.getDescription())
                        .lastStudiedAt(ti.getLastStudiedAt())
                        .studyCount(ti.getStudyCount()).totalStudySeconds(ti.getTotalStudySeconds())
                        .orderIndex(ti.getOrderIndex())
                        .createdAt(ti.getCreatedAt()).updatedAt(ti.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.StudySessionEntry> sessions = studySessionRepository.findAll().stream()
                .map(s -> BackupSnapshotDto.StudySessionEntry.builder()
                        .id(s.getId()).topicItemId(s.getTopicItem().getId())
                        .studyDate(s.getStudyDate()).durationSeconds(s.getDurationSeconds())
                        .notes(s.getNotes()).createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.NoteEntry> notes = noteRepository.findAll().stream()
                .map(n -> BackupSnapshotDto.NoteEntry.builder()
                        .id(n.getId()).title(n.getTitle()).content(n.getContent())
                        .subjectId(n.getSubjectId()).topicId(n.getTopicId()).topicItemId(n.getTopicItemId())
                        .createdAt(n.getCreatedAt()).updatedAt(n.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.LibraryItemEntry> libraryItems = libraryItemRepository.findAll().stream()
                .map(li -> BackupSnapshotDto.LibraryItemEntry.builder()
                        .id(li.getId()).title(li.getTitle()).content(li.getContent())
                        .url(li.getUrl()).author(li.getAuthor())
                        .type(li.getType().name()).subjectId(li.getSubjectId())
                        .tags(li.getTags())
                        .createdAt(li.getCreatedAt()).updatedAt(li.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        List<BackupSnapshotDto.WeeklyPlanEntry> weeklyPlans = weeklyPlanRepository.findAll().stream()
                .map(wp -> BackupSnapshotDto.WeeklyPlanEntry.builder()
                        .id(wp.getId()).topicItemId(wp.getTopicItem().getId())
                        .dayOfWeek(wp.getDayOfWeek()).plannedMinutes(wp.getPlannedMinutes())
                        .orderIndex(wp.getOrderIndex()).createdAt(wp.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return BackupSnapshotDto.builder()
                .timestamp(timestamp).version("1.0")
                .subjects(subjects).topics(topics).topicItems(topicItems)
                .studySessions(sessions).notes(notes)
                .libraryItems(libraryItems).weeklyPlans(weeklyPlans)
                .build();
    }

    public String exportSnapshot() throws IOException, InterruptedException {
        BackupSnapshotDto snapshot = buildSnapshot();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "snapshot_" + timestamp + ".json";

        File historyDir = new File(historyPath);
        historyDir.mkdirs();

        File snapshotFile = new File(historyDir, filename);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(snapshotFile, snapshot);

        log.info("Snapshot saved: {}", snapshotFile.getAbsolutePath());

        try {
            runGit(new String[]{"git", "add", "history/"}, projectRoot);
            runGit(new String[]{"git", "commit", "-m", "backup: snapshot " + timestamp}, projectRoot);
            log.info("Git commit done for snapshot: {}", filename);
        } catch (Exception e) {
            log.warn("Git commit skipped: {}", e.getMessage());
        }

        return filename;
    }

    @Transactional
    public String importLatestSnapshot() throws IOException {
        File historyDir = new File(historyPath);
        if (!historyDir.exists() || !historyDir.isDirectory()) {
            throw new RuntimeException("Pasta history não encontrada: " + historyDir.getAbsolutePath());
        }

        File[] files = historyDir.listFiles((d, name) -> name.startsWith("snapshot_") && name.endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new RuntimeException("Nenhum snapshot encontrado em: " + historyDir.getAbsolutePath());
        }

        File latest = Arrays.stream(files)
                .max(Comparator.comparing(File::getName))
                .orElseThrow();

        BackupSnapshotDto snapshot = objectMapper.readValue(latest, BackupSnapshotDto.class);
        restoreSnapshot(snapshot);
        return latest.getName();
    }

    private void restoreSnapshot(BackupSnapshotDto snapshot) {
        // Clear in reverse dependency order
        weeklyPlanRepository.deleteAll();
        studySessionRepository.deleteAll();
        topicItemRepository.deleteAll();
        topicRepository.deleteAll();
        subjectRepository.deleteAll();
        noteRepository.deleteAll();
        libraryItemRepository.deleteAll();

        Map<Long, Long> subjectIdMap = new HashMap<>();
        Map<Long, Long> topicIdMap = new HashMap<>();
        Map<Long, Long> topicItemIdMap = new HashMap<>();

        // Subjects
        for (BackupSnapshotDto.SubjectEntry e : snapshot.getSubjects()) {
            Subject s = Subject.builder()
                    .name(e.getName()).description(e.getDescription())
                    .color(e.getColor() != null ? e.getColor() : "#6366f1")
                    .emoji(e.getEmoji() != null ? e.getEmoji() : "📚")
                    .build();
            Subject saved = subjectRepository.save(s);
            subjectIdMap.put(e.getId(), saved.getId());
        }

        // Topics
        for (BackupSnapshotDto.TopicEntry e : snapshot.getTopics()) {
            Long newSubjectId = subjectIdMap.get(e.getSubjectId());
            if (newSubjectId == null) continue;
            Subject subject = subjectRepository.findById(newSubjectId).orElse(null);
            if (subject == null) continue;
            Topic t = Topic.builder()
                    .name(e.getName()).description(e.getDescription())
                    .weekDays(e.getWeekDays() != null ? e.getWeekDays() : "")
                    .orderIndex(e.getOrderIndex() != null ? e.getOrderIndex() : 0)
                    .subject(subject)
                    .build();
            Topic saved = topicRepository.save(t);
            topicIdMap.put(e.getId(), saved.getId());
        }

        // Topic Items
        for (BackupSnapshotDto.TopicItemEntry e : snapshot.getTopicItems()) {
            Long newTopicId = topicIdMap.get(e.getTopicId());
            if (newTopicId == null) continue;
            Topic topic = topicRepository.findById(newTopicId).orElse(null);
            if (topic == null) continue;
            TopicItem ti = TopicItem.builder()
                    .name(e.getName()).description(e.getDescription())
                    .lastStudiedAt(e.getLastStudiedAt())
                    .studyCount(e.getStudyCount() != null ? e.getStudyCount() : 0)
                    .totalStudySeconds(e.getTotalStudySeconds() != null ? e.getTotalStudySeconds() : 0L)
                    .orderIndex(e.getOrderIndex() != null ? e.getOrderIndex() : 0)
                    .topic(topic)
                    .build();
            TopicItem saved = topicItemRepository.save(ti);
            topicItemIdMap.put(e.getId(), saved.getId());
        }

        // Study Sessions
        for (BackupSnapshotDto.StudySessionEntry e : snapshot.getStudySessions()) {
            Long newItemId = topicItemIdMap.get(e.getTopicItemId());
            if (newItemId == null) continue;
            TopicItem item = topicItemRepository.findById(newItemId).orElse(null);
            if (item == null) continue;
            StudySession s = StudySession.builder()
                    .topicItem(item).studyDate(e.getStudyDate())
                    .durationSeconds(e.getDurationSeconds() != null ? e.getDurationSeconds() : 0L)
                    .notes(e.getNotes())
                    .build();
            studySessionRepository.save(s);
        }

        // Notes
        for (BackupSnapshotDto.NoteEntry e : snapshot.getNotes()) {
            Note n = Note.builder()
                    .title(e.getTitle()).content(e.getContent())
                    .subjectId(e.getSubjectId() != null ? subjectIdMap.get(e.getSubjectId()) : null)
                    .topicId(e.getTopicId() != null ? topicIdMap.get(e.getTopicId()) : null)
                    .topicItemId(e.getTopicItemId() != null ? topicItemIdMap.get(e.getTopicItemId()) : null)
                    .build();
            noteRepository.save(n);
        }

        // Library Items
        for (BackupSnapshotDto.LibraryItemEntry e : snapshot.getLibraryItems()) {
            LibraryItem li = LibraryItem.builder()
                    .title(e.getTitle()).content(e.getContent())
                    .url(e.getUrl()).author(e.getAuthor())
                    .type(LibraryItem.ItemType.valueOf(e.getType()))
                    .subjectId(e.getSubjectId() != null ? subjectIdMap.get(e.getSubjectId()) : null)
                    .tags(e.getTags())
                    .build();
            libraryItemRepository.save(li);
        }

        // Weekly Plans
        for (BackupSnapshotDto.WeeklyPlanEntry e : snapshot.getWeeklyPlans()) {
            Long newItemId = topicItemIdMap.get(e.getTopicItemId());
            if (newItemId == null) continue;
            TopicItem item = topicItemRepository.findById(newItemId).orElse(null);
            if (item == null) continue;
            WeeklyPlan wp = WeeklyPlan.builder()
                    .topicItem(item).dayOfWeek(e.getDayOfWeek())
                    .plannedMinutes(e.getPlannedMinutes() != null ? e.getPlannedMinutes() : 30)
                    .orderIndex(e.getOrderIndex() != null ? e.getOrderIndex() : 0)
                    .build();
            weeklyPlanRepository.save(wp);
        }
    }

    private void runGit(String[] command, String workingDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String output = new String(process.getInputStream().readAllBytes());
            throw new RuntimeException("Git command failed: " + String.join(" ", command) + "\n" + output);
        }
    }
}
