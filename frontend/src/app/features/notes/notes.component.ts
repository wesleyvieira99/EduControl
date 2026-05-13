import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Note, Subject } from '../../core/models/models';

@Component({
  selector: 'app-notes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './notes.component.html',
  styleUrls: ['./notes.component.scss']
})
export class NotesComponent implements OnInit {
  notes: Note[] = [];
  subjects: Subject[] = [];
  loading = false;

  search = '';
  filterSubjectId: number | null = null;

  showModal = false;
  editingNote: Note | null = null;
  noteForm: Partial<Note> = { title: '', content: '', subjectId: undefined };

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getSubjects().subscribe(s => this.subjects = s);
    this.loadNotes();
  }

  loadNotes() {
    this.loading = true;
    const params: any = {};
    if (this.filterSubjectId) params.subjectId = this.filterSubjectId;
    if (this.search.trim()) params.search = this.search.trim();
    this.api.getNotes(params).subscribe({ next: n => { this.notes = n; this.loading = false; }, error: () => this.loading = false });
  }

  openCreate() {
    this.editingNote = null;
    this.noteForm = { title: '', content: '', subjectId: undefined };
    this.showModal = true;
  }

  openEdit(n: Note) {
    this.editingNote = n;
    this.noteForm = { ...n };
    this.showModal = true;
  }

  save() {
    if (!this.noteForm.title?.trim()) return;
    const obs = this.editingNote
      ? this.api.updateNote(this.editingNote.id!, this.noteForm as Note)
      : this.api.createNote(this.noteForm as Note);
    obs.subscribe(() => { this.loadNotes(); this.showModal = false; });
  }

  delete(n: Note) {
    if (!confirm(`Excluir "${n.title}"?`)) return;
    this.api.deleteNote(n.id!).subscribe(() => this.loadNotes());
  }

  getSubjectName(id: number | undefined): string {
    if (!id) return '';
    return this.subjects.find(s => s.id === id)?.name || '';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('pt-BR', { day:'2-digit', month:'short', year:'2-digit' });
  }
}
