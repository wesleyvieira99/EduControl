import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Subject, Topic, TopicItem } from '../../core/models/models';

@Component({
  selector: 'app-subjects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subjects.component.html',
  styleUrls: ['./subjects.component.scss']
})
export class SubjectsComponent implements OnInit {
  subjects: Subject[] = [];
  selectedSubject: Subject | null = null;
  selectedTopic: Topic | null = null;
  topics: Topic[] = [];
  topicItems: TopicItem[] = [];

  loading = false;

  // Modals
  showSubjectModal = false;
  showTopicModal = false;
  showItemModal = false;

  // Forms
  subjectForm: Partial<Subject> = { name: '', description: '', color: '#6366f1', emoji: '📚' };
  topicForm: Partial<Topic> = { name: '', description: '', weekDays: '', orderIndex: 0 };
  itemForm: Partial<TopicItem> = { name: '', description: '', orderIndex: 0 };

  editingSubject: Subject | null = null;
  editingTopic: Topic | null = null;
  editingItem: TopicItem | null = null;

  colorOptions = ['#6366f1','#8b5cf6','#ec4899','#f43f5e','#f97316','#eab308','#22c55e','#14b8a6','#3b82f6','#06b6d4'];
  emojiOptions = ['📚','🔬','🎨','💻','📐','🌍','📖','🎵','⚗️','🏛️','📊','🧬'];

  weekDayLabels = ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'];
  selectedWeekDays: boolean[] = [false,false,false,false,false,false,false];

  constructor(private api: ApiService) {}

  ngOnInit() { this.loadSubjects(); }

  loadSubjects() {
    this.loading = true;
    this.api.getSubjects().subscribe({ next: s => { this.subjects = s; this.loading = false; }, error: () => this.loading = false });
  }

  selectSubject(s: Subject) {
    this.selectedSubject = s;
    this.selectedTopic = null;
    this.topicItems = [];
    this.api.getTopics(s.id!).subscribe(t => this.topics = t);
  }

  selectTopic(t: Topic) {
    this.selectedTopic = t;
    this.api.getTopicItems(t.id!).subscribe(i => this.topicItems = i);
  }

  // Subject CRUD
  openCreateSubject() {
    this.editingSubject = null;
    this.subjectForm = { name: '', description: '', color: '#6366f1', emoji: '📚' };
    this.showSubjectModal = true;
  }
  openEditSubject(s: Subject, e: Event) {
    e.stopPropagation();
    this.editingSubject = s;
    this.subjectForm = { ...s };
    this.showSubjectModal = true;
  }
  saveSubject() {
    if (!this.subjectForm.name?.trim()) return;
    const obs = this.editingSubject
      ? this.api.updateSubject(this.editingSubject.id!, this.subjectForm as Subject)
      : this.api.createSubject(this.subjectForm as Subject);
    obs.subscribe(() => { this.loadSubjects(); this.showSubjectModal = false; });
  }
  deleteSubject(s: Subject, e: Event) {
    e.stopPropagation();
    if (!confirm(`Excluir "${s.name}" e todos os seus temas?`)) return;
    this.api.deleteSubject(s.id!).subscribe(() => {
      this.loadSubjects();
      if (this.selectedSubject?.id === s.id) { this.selectedSubject = null; this.topics = []; }
    });
  }

  // Topic CRUD
  openCreateTopic() {
    this.editingTopic = null;
    this.topicForm = { name: '', description: '', weekDays: '', orderIndex: 0 };
    this.selectedWeekDays = [false,false,false,false,false,false,false];
    this.showTopicModal = true;
  }
  openEditTopic(t: Topic, e: Event) {
    e.stopPropagation();
    this.editingTopic = t;
    this.topicForm = { ...t };
    this.selectedWeekDays = [false,false,false,false,false,false,false];
    if (t.weekDays) t.weekDays.split(',').forEach(d => {
      const n = parseInt(d.trim());
      if (!isNaN(n) && n >= 0 && n <= 6) this.selectedWeekDays[n] = true;
    });
    this.showTopicModal = true;
  }
  saveTopic() {
    if (!this.topicForm.name?.trim() || !this.selectedSubject) return;
    const weekDays = this.selectedWeekDays.map((v,i) => v ? i : -1).filter(i => i >= 0).join(',');
    const topic = { ...this.topicForm, weekDays } as Topic;
    const obs = this.editingTopic
      ? this.api.updateTopic(this.editingTopic.id!, topic)
      : this.api.createTopic(this.selectedSubject.id!, topic);
    obs.subscribe(() => { this.api.getTopics(this.selectedSubject!.id!).subscribe(t => this.topics = t); this.showTopicModal = false; });
  }
  deleteTopic(t: Topic, e: Event) {
    e.stopPropagation();
    if (!confirm(`Excluir tema "${t.name}"?`)) return;
    this.api.deleteTopic(t.id!).subscribe(() => {
      this.api.getTopics(this.selectedSubject!.id!).subscribe(t => this.topics = t);
      if (this.selectedTopic?.id === t.id) { this.selectedTopic = null; this.topicItems = []; }
    });
  }
  toggleWeekDay(i: number) { this.selectedWeekDays[i] = !this.selectedWeekDays[i]; }

  // TopicItem CRUD
  openCreateItem() {
    this.editingItem = null;
    this.itemForm = { name: '', description: '', orderIndex: 0 };
    this.showItemModal = true;
  }
  openEditItem(item: TopicItem, e: Event) {
    e.stopPropagation();
    this.editingItem = item;
    this.itemForm = { ...item };
    this.showItemModal = true;
  }
  saveItem() {
    if (!this.itemForm.name?.trim() || !this.selectedTopic) return;
    const obs = this.editingItem
      ? this.api.updateTopicItem(this.editingItem.id!, this.itemForm as TopicItem)
      : this.api.createTopicItem(this.selectedTopic.id!, this.itemForm as TopicItem);
    obs.subscribe(() => { this.api.getTopicItems(this.selectedTopic!.id!).subscribe(i => this.topicItems = i); this.showItemModal = false; });
  }
  deleteItem(item: TopicItem, e: Event) {
    e.stopPropagation();
    if (!confirm(`Excluir item "${item.name}"?`)) return;
    this.api.deleteTopicItem(item.id!).subscribe(() => this.api.getTopicItems(this.selectedTopic!.id!).subscribe(i => this.topicItems = i));
  }

  formatTime(seconds: number): string {
    if (!seconds) return '—';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  formatLastStudied(dateStr: string | undefined): string {
    if (!dateStr) return 'Nunca estudado';
    const d = new Date(dateStr);
    return d.toLocaleDateString('pt-BR');
  }

  getWeekDayBadges(weekDays: string): string[] {
    if (!weekDays) return [];
    return weekDays.split(',').map(d => this.weekDayLabels[parseInt(d.trim())] || '').filter(Boolean);
  }
}
