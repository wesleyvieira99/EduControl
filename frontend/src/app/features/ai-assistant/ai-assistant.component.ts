import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Subject, Topic, TopicItem, AIRequest } from '../../core/models/models';

interface Message { role: 'user'|'ai'; content: string; time: Date; }

@Component({
  selector: 'app-ai-assistant',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai-assistant.component.html',
  styleUrls: ['./ai-assistant.component.scss']
})
export class AiAssistantComponent implements OnInit {
  subjects: Subject[] = [];
  topics: Topic[] = [];
  topicItems: TopicItem[] = [];

  selectedSubjectId: number | null = null;
  selectedTopicId: number | null = null;
  selectedItemId: number | null = null;

  request: AIRequest = {
    subjectName: '', topicName: '', topicItemName: '',
    type: 'questions', quantity: 5, difficulty: 'medium', additionalContext: ''
  };

  types: {value: AIRequest['type']; label: string; desc: string}[] = [
    { value: 'questions',  label: '❓ Perguntas',   desc: 'Gera questões para revisão' },
    { value: 'exercises',  label: '✏️ Exercícios',  desc: 'Exercícios práticos' },
    { value: 'summary',    label: '📋 Resumo',      desc: 'Resumo do conteúdo' },
    { value: 'flashcards', label: '🃏 Flashcards',  desc: 'Cartões de memorização' },
  ];

  difficulties: {value: 'easy'|'medium'|'hard'; label: string}[] = [
    { value: 'easy',   label: '😌 Fácil' },
    { value: 'medium', label: '🤔 Médio' },
    { value: 'hard',   label: '🔥 Difícil' },
  ];

  messages: Message[] = [];
  loading = false;
  result = '';

  constructor(private api: ApiService) {}

  ngOnInit() { this.api.getSubjects().subscribe(s => this.subjects = s); }

  onSubjectChange() {
    this.selectedTopicId = null; this.selectedItemId = null;
    this.topics = []; this.topicItems = [];
    const s = this.subjects.find(s => s.id === this.selectedSubjectId);
    this.request.subjectName = s?.name || '';
    this.request.topicName = '';
    this.request.topicItemName = '';
    if (this.selectedSubjectId) this.api.getTopics(this.selectedSubjectId).subscribe(t => this.topics = t);
  }

  onTopicChange() {
    this.selectedItemId = null; this.topicItems = [];
    const t = this.topics.find(t => t.id === this.selectedTopicId);
    this.request.topicName = t?.name || '';
    this.request.topicItemName = '';
    if (this.selectedTopicId) this.api.getTopicItems(this.selectedTopicId).subscribe(i => this.topicItems = i);
  }

  onItemChange() {
    const item = this.topicItems.find(i => i.id === this.selectedItemId);
    this.request.topicItemName = item?.name || '';
  }

  generate() {
    if (!this.request.subjectName && !this.request.topicName) return;
    this.loading = true;
    this.result = '';
    this.api.generateAI(this.request).subscribe({
      next: res => {
        this.loading = false;
        this.result = res.content || res.error || 'Sem resposta.';
        this.messages.push({ role: 'ai', content: this.result, time: new Date() });
      },
      error: () => {
        this.loading = false;
        this.result = '⚠️ Erro ao conectar com a IA. Verifique sua chave de API.';
        this.messages.push({ role: 'ai', content: this.result, time: new Date() });
      }
    });
    this.messages.push({ role: 'user', content: `${this.getTypeLabel()} sobre "${this.request.topicItemName || this.request.topicName || this.request.subjectName}" (${this.request.difficulty}, ${this.request.quantity}x)`, time: new Date() });
  }

  getTypeLabel(): string { return this.types.find(t => t.value === this.request.type)?.label || ''; }

  copyResult() {
    if (this.result) navigator.clipboard.writeText(this.result).then(() => {});
  }

  clearHistory() { this.messages = []; this.result = ''; }

  formatContent(text: string): string {
    return text
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/^(\d+\.) /gm, '<br><strong>$1</strong> ')
      .replace(/^- /gm, '<br>• ')
      .replace(/\n\n/g, '<br><br>')
      .replace(/\n/g, '<br>');
  }
}
