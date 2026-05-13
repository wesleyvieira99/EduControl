export interface Subject {
  id?: number;
  name: string;
  description?: string;
  color: string;
  emoji: string;
  createdAt?: string;
  updatedAt?: string;
  topics?: Topic[];
}

export interface Topic {
  id?: number;
  name: string;
  description?: string;
  subjectId?: number;
  subject?: Subject;
  weekDays: string; // "1,2,3" => Monday, Tuesday, Wednesday
  orderIndex: number;
  createdAt?: string;
  items?: TopicItem[];
}

export interface TopicItem {
  id?: number;
  name: string;
  description?: string;
  topicId?: number;
  topic?: Topic;
  lastStudiedAt?: string;
  studyCount: number;
  totalStudySeconds: number;
  orderIndex: number;
  createdAt?: string;
}

export interface StudySession {
  id?: number;
  topicItemId?: number;
  topicItem?: TopicItem;
  studyDate: string;
  durationSeconds: number;
  notes?: string;
  createdAt?: string;
}

export interface Note {
  id?: number;
  title: string;
  content: string;
  subjectId?: number;
  topicId?: number;
  topicItemId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface LibraryItem {
  id?: number;
  title: string;
  content?: string;
  url?: string;
  author?: string;
  type: 'ARTICLE' | 'BOOK' | 'EXCERPT' | 'INFO' | 'LINK' | 'VIDEO';
  subjectId?: number;
  tags?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface WeeklyPlan {
  id?: number;
  topicId?: number;
  dayOfWeek: number;
  plannedMinutes: number;
  orderIndex: number;
  createdAt?: string;
  // Contexto flat retornado pelo backend DTO
  topicName?: string;
  subjectId?: number;
  subjectName?: string;
  subjectColor?: string;
  subjectEmoji?: string;
}

export interface DashboardData {
  totalStudySecondsToday: number;
  totalStudySecondsWeek: number;
  totalStudySecondsMonth: number;
  totalSessionsToday: number;
  totalSubjects: number;
  totalTopics: number;
  totalTopicItems: number;
  weeklyStats: DailyStat[];
  subjectStats: SubjectStat[];
  recentActivity: RecentActivity[];
}

export interface DailyStat {
  date: string;
  durationSeconds: number;
  sessions?: number;
}

export interface SubjectStat {
  subjectId: number;
  subjectName: string;
  color: string;
  totalSeconds: number;
  sessionsCount: number;
}

export interface RecentActivity {
  sessionId: number;
  topicItemName: string;
  topicName: string;
  subjectName: string;
  subjectColor: string;
  studyDate: string;
  durationSeconds: number;
}

export interface CalendarEvent {
  date: string;
  totalSeconds: number;
  sessionsCount?: number;
  level: 'none' | 'low' | 'medium' | 'high';
}

export interface AIRequest {
  subjectName?: string;
  topicName?: string;
  topicItemName?: string;
  type: 'questions' | 'exercises' | 'summary' | 'flashcards';
  quantity: number;
  difficulty: 'easy' | 'medium' | 'hard';
  additionalContext?: string;
}

export interface AIResponse {
  content: string;
  success: boolean;
  error?: string;
}

export interface BackupResult {
  filename: string;
  message: string;
  error?: string;
}
