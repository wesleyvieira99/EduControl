import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { LibraryItem, Subject } from '../../core/models/models';

type LibraryType = 'ARTICLE'|'BOOK'|'EXCERPT'|'INFO'|'LINK'|'VIDEO';

@Component({
  selector: 'app-library',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './library.component.html',
  styleUrls: ['./library.component.scss']
})
export class LibraryComponent implements OnInit {
  items: LibraryItem[] = [];
  subjects: Subject[] = [];
  loading = false;

  search = '';
  filterType: LibraryType | '' = '';
  filterSubjectId: number | null = null;

  showModal = false;
  editingItem: LibraryItem | null = null;
  itemForm: Partial<LibraryItem> = {};

  types: {value: LibraryType; label: string; icon: string}[] = [
    { value: 'ARTICLE', label: 'Artigo',  icon: '📄' },
    { value: 'BOOK',    label: 'Livro',   icon: '📖' },
    { value: 'EXCERPT', label: 'Excerto', icon: '✂️' },
    { value: 'INFO',    label: 'Info',    icon: 'ℹ️' },
    { value: 'LINK',    label: 'Link',    icon: '🔗' },
    { value: 'VIDEO',   label: 'Vídeo',   icon: '🎥' },
  ];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getSubjects().subscribe(s => this.subjects = s);
    this.loadItems();
  }

  loadItems() {
    this.loading = true;
    const params: any = {};
    if (this.filterSubjectId) params.subjectId = this.filterSubjectId;
    if (this.filterType) params.type = this.filterType;
    if (this.search.trim()) params.search = this.search.trim();
    this.api.getLibraryItems(params).subscribe({ next: i => { this.items = i; this.loading = false; }, error: () => this.loading = false });
  }

  openCreate() {
    this.editingItem = null;
    this.itemForm = { type: 'ARTICLE', title: '', content: '', url: '', author: '', tags: '' };
    this.showModal = true;
  }

  openEdit(item: LibraryItem) {
    this.editingItem = item;
    this.itemForm = { ...item };
    this.showModal = true;
  }

  save() {
    if (!this.itemForm.title?.trim()) return;
    const obs = this.editingItem
      ? this.api.updateLibraryItem(this.editingItem.id!, this.itemForm as LibraryItem)
      : this.api.createLibraryItem(this.itemForm as LibraryItem);
    obs.subscribe(() => { this.loadItems(); this.showModal = false; });
  }

  delete(item: LibraryItem) {
    if (!confirm(`Excluir "${item.title}"?`)) return;
    this.api.deleteLibraryItem(item.id!).subscribe(() => this.loadItems());
  }

  getTypeInfo(type: string) {
    return this.types.find(t => t.value === type) || { icon: '📄', label: type };
  }

  getSubjectName(id: number | undefined): string {
    if (!id) return '';
    return this.subjects.find(s => s.id === id)?.name || '';
  }

  openUrl(url: string | undefined, e: Event) {
    e.stopPropagation();
    if (url) window.open(url, '_blank', 'noopener,noreferrer');
  }
}
