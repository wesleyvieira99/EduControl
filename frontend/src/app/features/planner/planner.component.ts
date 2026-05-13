import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { WeeklyPlan, TopicItem, Subject, Topic } from '../../core/models/models';

interface DaySlot { label: string; short: string; index: number; plans: WeeklyPlan[]; }

@Component({
  selector: 'app-planner',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './planner.component.html',
  styleUrls: ['./planner.component.scss']
})
export class PlannerComponent implements OnInit {
  days: DaySlot[] = [
    { label: 'Domingo', short: 'Dom', index: 0, plans: [] },
    { label: 'Segunda', short: 'Seg', index: 1, plans: [] },
    { label: 'Terça',   short: 'Ter', index: 2, plans: [] },
    { label: 'Quarta',  short: 'Qua', index: 3, plans: [] },
    { label: 'Quinta',  short: 'Qui', index: 4, plans: [] },
    { label: 'Sexta',   short: 'Sex', index: 5, plans: [] },
    { label: 'Sábado',  short: 'Sáb', index: 6, plans: [] },
  ];

  todayIndex = new Date().getDay();

  subjects: Subject[] = [];
  topics: Topic[] = [];
  topicItems: TopicItem[] = [];

  showModal = false;
  editingPlan: WeeklyPlan | null = null;
  selectedDayIndex = 1;

  planForm: Partial<WeeklyPlan> = { plannedMinutes: 30 };
  modalSubjectId: number | null = null;
  modalTopicId: number | null = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getSubjects().subscribe(s => this.subjects = s);
    this.loadAllPlans();
  }

  loadAllPlans() {
    this.days.forEach(d => {
      this.api.getPlansByDay(d.index).subscribe(plans => d.plans = plans);
    });
  }

  openAddPlan(dayIndex: number) {
    this.editingPlan = null;
    this.selectedDayIndex = dayIndex;
    this.planForm = { plannedMinutes: 30 };
    this.modalSubjectId = null;
    this.modalTopicId = null;
    this.topics = [];
    this.topicItems = [];
    this.showModal = true;
  }

  openEditPlan(plan: WeeklyPlan, dayIndex: number) {
    this.editingPlan = plan;
    this.selectedDayIndex = dayIndex;
    this.planForm = { ...plan };
    this.showModal = true;
  }

  onModalSubjectChange() {
    this.modalTopicId = null;
    this.topicItems = [];
    if (this.modalSubjectId) {
      this.api.getTopics(this.modalSubjectId).subscribe(t => this.topics = t);
    }
  }

  onModalTopicChange() {
    this.topicItems = [];
    if (this.modalTopicId) {
      this.api.getTopicItems(this.modalTopicId).subscribe(i => this.topicItems = i);
    }
  }

  savePlan() {
    if (!this.planForm.topicItemId && !this.editingPlan) return;
    const plan: any = { ...this.planForm, dayOfWeek: this.selectedDayIndex };

    const obs = this.editingPlan
      ? this.api.updatePlan(this.editingPlan.id!, plan)
      : this.api.createPlan(plan.topicItemId, plan);

    obs.subscribe(() => {
      this.api.getPlansByDay(this.selectedDayIndex).subscribe(plans => {
        this.days[this.selectedDayIndex].plans = plans;
      });
      this.showModal = false;
    });
  }

  deletePlan(plan: WeeklyPlan, dayIndex: number) {
    if (!confirm('Remover este item do planejamento?')) return;
    this.api.deletePlan(plan.id!).subscribe(() => {
      this.api.getPlansByDay(dayIndex).subscribe(plans => this.days[dayIndex].plans = plans);
    });
  }

  totalMinutes(day: DaySlot): number {
    return day.plans.reduce((a, p) => a + (p.plannedMinutes || 0), 0);
  }
}
