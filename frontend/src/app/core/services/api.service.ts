import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Subject, Topic, TopicItem, StudySession,
  Note, LibraryItem, WeeklyPlan, DashboardData,
  CalendarEvent, AIRequest, AIResponse, BackupResult
} from '../models/models';

const API = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  // ── Subjects ──────────────────────────────────────────────────────────────
  getSubjects(): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${API}/subjects`);
  }
  getSubject(id: number): Observable<Subject> {
    return this.http.get<Subject>(`${API}/subjects/${id}`);
  }
  createSubject(data: Subject): Observable<Subject> {
    return this.http.post<Subject>(`${API}/subjects`, data);
  }
  updateSubject(id: number, data: Subject): Observable<Subject> {
    return this.http.put<Subject>(`${API}/subjects/${id}`, data);
  }
  deleteSubject(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/subjects/${id}`);
  }

  // ── Topics ────────────────────────────────────────────────────────────────
  getTopics(subjectId: number): Observable<Topic[]> {
    return this.http.get<Topic[]>(`${API}/topics`, { params: new HttpParams().set('subjectId', subjectId) });
  }
  getTopic(id: number): Observable<Topic> {
    return this.http.get<Topic>(`${API}/topics/${id}`);
  }
  createTopic(subjectId: number, data: Topic): Observable<Topic> {
    return this.http.post<Topic>(`${API}/topics`, data, { params: new HttpParams().set('subjectId', subjectId) });
  }
  updateTopic(id: number, data: Topic): Observable<Topic> {
    return this.http.put<Topic>(`${API}/topics/${id}`, data);
  }
  deleteTopic(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/topics/${id}`);
  }

  // ── Topic Items ───────────────────────────────────────────────────────────
  getTopicItems(topicId: number): Observable<TopicItem[]> {
    return this.http.get<TopicItem[]>(`${API}/topic-items`, { params: new HttpParams().set('topicId', topicId) });
  }
  getTopicItem(id: number): Observable<TopicItem> {
    return this.http.get<TopicItem>(`${API}/topic-items/${id}`);
  }
  createTopicItem(topicId: number, data: TopicItem): Observable<TopicItem> {
    return this.http.post<TopicItem>(`${API}/topic-items`, data, { params: new HttpParams().set('topicId', topicId) });
  }
  updateTopicItem(id: number, data: TopicItem): Observable<TopicItem> {
    return this.http.put<TopicItem>(`${API}/topic-items/${id}`, data);
  }
  deleteTopicItem(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/topic-items/${id}`);
  }

  // ── Study Sessions ────────────────────────────────────────────────────────
  getSessionsByItem(topicItemId: number): Observable<StudySession[]> {
    return this.http.get<StudySession[]>(`${API}/sessions/by-item`, { params: new HttpParams().set('topicItemId', topicItemId) });
  }
  getSessionsByDate(date: string): Observable<StudySession[]> {
    return this.http.get<StudySession[]>(`${API}/sessions/by-date`, { params: new HttpParams().set('date', date) });
  }
  getSessionsByRange(from: string, to: string): Observable<StudySession[]> {
    return this.http.get<StudySession[]>(`${API}/sessions/range`, { params: new HttpParams().set('from', from).set('to', to) });
  }
  createSession(topicItemId: number, data: StudySession): Observable<StudySession> {
    return this.http.post<StudySession>(`${API}/sessions`, data, { params: new HttpParams().set('topicItemId', topicItemId) });
  }
  updateSession(id: number, data: StudySession): Observable<StudySession> {
    return this.http.put<StudySession>(`${API}/sessions/${id}`, data);
  }
  deleteSession(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/sessions/${id}`);
  }

  // ── Notes ─────────────────────────────────────────────────────────────────
  getNotes(filters?: { subjectId?: number; topicId?: number; topicItemId?: number; search?: string }): Observable<Note[]> {
    let params = new HttpParams();
    if (filters?.subjectId) params = params.set('subjectId', filters.subjectId);
    if (filters?.topicId) params = params.set('topicId', filters.topicId);
    if (filters?.topicItemId) params = params.set('topicItemId', filters.topicItemId);
    if (filters?.search) params = params.set('search', filters.search);
    return this.http.get<Note[]>(`${API}/notes`, { params });
  }
  createNote(data: Note): Observable<Note> {
    return this.http.post<Note>(`${API}/notes`, data);
  }
  updateNote(id: number, data: Note): Observable<Note> {
    return this.http.put<Note>(`${API}/notes/${id}`, data);
  }
  deleteNote(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/notes/${id}`);
  }

  // ── Library ───────────────────────────────────────────────────────────────
  getLibrary(filters?: { subjectId?: number; type?: string; search?: string }): Observable<LibraryItem[]> {
    let params = new HttpParams();
    if (filters?.subjectId) params = params.set('subjectId', filters.subjectId);
    if (filters?.type) params = params.set('type', filters.type);
    if (filters?.search) params = params.set('search', filters.search);
    return this.http.get<LibraryItem[]>(`${API}/library`, { params });
  }
  getLibraryItems(filters?: { subjectId?: number; type?: string; search?: string }): Observable<LibraryItem[]> {
    return this.getLibrary(filters);
  }
  createLibraryItem(data: LibraryItem): Observable<LibraryItem> {
    return this.http.post<LibraryItem>(`${API}/library`, data);
  }
  updateLibraryItem(id: number, data: LibraryItem): Observable<LibraryItem> {
    return this.http.put<LibraryItem>(`${API}/library/${id}`, data);
  }
  deleteLibraryItem(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/library/${id}`);
  }

  // ── Weekly Plans ──────────────────────────────────────────────────────────
  getWeeklyPlansByDay(dayOfWeek: number): Observable<WeeklyPlan[]> {
    return this.http.get<WeeklyPlan[]>(`${API}/weekly-plans`, { params: new HttpParams().set('dayOfWeek', dayOfWeek) });
  }
  getPlansByDay(dayOfWeek: number): Observable<WeeklyPlan[]> { return this.getWeeklyPlansByDay(dayOfWeek); }
  createWeeklyPlan(topicItemId: number, data: WeeklyPlan): Observable<WeeklyPlan> {
    return this.http.post<WeeklyPlan>(`${API}/weekly-plans`, data, { params: new HttpParams().set('topicItemId', topicItemId) });
  }
  createPlan(topicItemId: number, data: any): Observable<WeeklyPlan> { return this.createWeeklyPlan(topicItemId, data); }
  updateWeeklyPlan(id: number, data: WeeklyPlan): Observable<WeeklyPlan> {
    return this.http.put<WeeklyPlan>(`${API}/weekly-plans/${id}`, data);
  }
  updatePlan(id: number, data: any): Observable<WeeklyPlan> { return this.updateWeeklyPlan(id, data); }
  deleteWeeklyPlan(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/weekly-plans/${id}`);
  }
  deletePlan(id: number): Observable<void> { return this.deleteWeeklyPlan(id); }

  // ── Analytics ─────────────────────────────────────────────────────────────
  getDashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${API}/analytics/dashboard`);
  }
  getCalendar(from?: string, to?: string): Observable<CalendarEvent[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<CalendarEvent[]>(`${API}/analytics/calendar`, { params });
  }
  getCalendarData(from: string, to: string): Observable<CalendarEvent[]> { return this.getCalendar(from, to); }

  // ── AI ────────────────────────────────────────────────────────────────────
  generateAI(request: AIRequest): Observable<AIResponse> {
    return this.http.post<AIResponse>(`${API}/ai/generate`, request);
  }

  // ── Backup ────────────────────────────────────────────────────────────────
  exportBackup(): Observable<BackupResult> {
    return this.http.post<BackupResult>(`${API}/backup/export`, {});
  }

  importBackup(): Observable<BackupResult> {
    return this.http.post<BackupResult>(`${API}/backup/import`, {});
  }

  // ── System ───────────────────────────────────────────────────────────────
  healthCheck(): Observable<{status: string}> {
    return this.http.get<{status: string}>(`${API}/system/health`);
  }

  restartApp(): Observable<{message: string}> {
    return this.http.post<{message: string}>(`${API}/system/restart`, {});
  }
}
