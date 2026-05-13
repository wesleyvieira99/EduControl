import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Subject, Topic, TopicItem } from '../../core/models/models';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-timer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './timer.component.html',
  styleUrls: ['./timer.component.scss']
})
export class TimerComponent implements OnInit, OnDestroy {
  subjects: Subject[] = [];
  topics: Topic[] = [];
  topicItems: TopicItem[] = [];

  selectedSubjectId: number | null = null;
  selectedTopicId: number | null = null;
  selectedItemId: number | null = null;
  selectedItem: TopicItem | null = null;
  sessionNotes = '';

  // Timer state
  isRunning = false;
  elapsed = 0; // seconds
  private timerSub?: Subscription;
  private startTime?: Date;

  // Sessions history for today
  todaySessions: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getSubjects().subscribe(s => this.subjects = s);
    this.loadTodaySessions();
  }

  ngOnDestroy() {
    this.timerSub?.unsubscribe();
  }

  onSubjectChange() {
    this.selectedTopicId = null;
    this.selectedItemId = null;
    this.selectedItem = null;
    this.topics = [];
    this.topicItems = [];
    if (this.selectedSubjectId) {
      this.api.getTopics(this.selectedSubjectId).subscribe(t => this.topics = t);
    }
  }

  onTopicChange() {
    this.selectedItemId = null;
    this.selectedItem = null;
    this.topicItems = [];
    if (this.selectedTopicId) {
      this.api.getTopicItems(this.selectedTopicId).subscribe(i => this.topicItems = i);
    }
  }

  onItemChange() {
    this.selectedItem = this.topicItems.find(i => i.id === this.selectedItemId) || null;
  }

  start() {
    if (!this.selectedItemId) return;
    this.isRunning = true;
    this.startTime = new Date();
    this.elapsed = 0;
    this.timerSub = interval(1000).subscribe(() => this.elapsed++);
  }

  pause() {
    this.isRunning = false;
    this.timerSub?.unsubscribe();
  }

  resume() {
    this.isRunning = true;
    this.timerSub = interval(1000).subscribe(() => this.elapsed++);
  }

  stop() {
    if (!this.selectedItemId || this.elapsed === 0) {
      this.reset();
      return;
    }
    this.timerSub?.unsubscribe();
    this.isRunning = false;

    const session = {
      durationSeconds: this.elapsed,
      notes: this.sessionNotes,
      studyDate: new Date().toISOString().split('T')[0]
    };

    this.api.createSession(this.selectedItemId, session as any).subscribe(() => {
      this.loadTodaySessions();
      this.reset();
    });
  }

  reset() {
    this.timerSub?.unsubscribe();
    this.isRunning = false;
    this.elapsed = 0;
    this.sessionNotes = '';
  }

  loadTodaySessions() {
    const today = new Date().toISOString().split('T')[0];
    this.api.getSessionsByDate(today).subscribe(s => this.todaySessions = s);
  }

  deleteSession(id: number) {
    if (!confirm('Excluir esta sessão?')) return;
    this.api.deleteSession(id).subscribe(() => this.loadTodaySessions());
  }

  get displayTime(): string {
    return this.formatTime(this.elapsed);
  }

  get totalTodayTime(): number {
    return this.todaySessions.reduce((a: number, s: any) => a + s.durationSeconds, 0);
  }

  formatTime(seconds: number): string {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    const hStr = h > 0 ? `${h.toString().padStart(2,'0')}:` : '';
    return `${hStr}${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}`;
  }

  formatMin(seconds: number): string {
    if (!seconds) return '0m';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  get timerProgress(): number {
    const target = 25 * 60; // 25 min pomodoro
    return Math.min(100, (this.elapsed / target) * 100);
  }

  get circumference(): number { return 2 * Math.PI * 110; }

  get dashOffset(): number {
    return this.circumference * (1 - this.timerProgress / 100);
  }
}
