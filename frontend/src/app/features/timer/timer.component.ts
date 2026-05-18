import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { TimerService } from '../../core/services/timer.service';
import { Subject, Topic, TopicItem } from '../../core/models/models';
import { Subscription } from 'rxjs';

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

  // Mirror of service state (updated via subscriptions)
  elapsed  = 0;
  isRunning = false;
  sessionNotes = '';

  todaySessions: any[] = [];

  private subs: Subscription[] = [];

  constructor(public timer: TimerService, private api: ApiService) {}

  ngOnInit() {
    this.api.getSubjects().subscribe(s => this.subjects = s);
    this.loadTodaySessions();

    // Mirror timer service state into local vars (drives the template)
    this.subs.push(
      this.timer.elapsed$.subscribe(e => this.elapsed = e),
      this.timer.isRunning$.subscribe(r => this.isRunning = r),
      this.timer.notes$.subscribe(n => this.sessionNotes = n),
      this.timer.sessionSaved$.subscribe(() => this.loadTodaySessions()),
    );
  }

  ngOnDestroy() {
    // Only unsubscribe UI subs — TimerService interval keeps running
    this.subs.forEach(s => s.unsubscribe());
  }

  onSubjectChange() {
    this.selectedTopicId = null;
    this.selectedItemId  = null;
    this.topics      = [];
    this.topicItems  = [];
    if (this.selectedSubjectId)
      this.api.getTopics(this.selectedSubjectId).subscribe(t => this.topics = t);
  }

  onTopicChange() {
    this.selectedItemId = null;
    this.topicItems = [];
    if (this.selectedTopicId)
      this.api.getTopicItems(this.selectedTopicId).subscribe(i => this.topicItems = i);
  }

  start() {
    if (!this.selectedItemId) return;
    const item = this.topicItems.find(i => i.id === this.selectedItemId)!;
    this.timer.selectItem(this.selectedItemId, item);
    this.timer.start();
  }

  pause()  { this.timer.pause(); }
  resume() { this.timer.resume(); }
  stop()   { this.timer.stop(); }
  reset()  { this.timer.reset(); }

  onNotesChange(notes: string) { this.timer.setNotes(notes); }

  loadTodaySessions() {
    const today = new Date().toISOString().split('T')[0];
    this.api.getSessionsByDate(today).subscribe(s => this.todaySessions = s);
  }

  deleteSession(id: number) {
    if (!confirm('Excluir esta sessão?')) return;
    this.api.deleteSession(id).subscribe(() => this.loadTodaySessions());
  }

  get displayTime(): string { return this.formatTime(this.elapsed); }

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
    const target = 25 * 60;
    return Math.min(100, (this.elapsed / target) * 100);
  }

  get circumference(): number { return 2 * Math.PI * 110; }

  get dashOffset(): number {
    return this.circumference * (1 - this.timerProgress / 100);
  }
}
