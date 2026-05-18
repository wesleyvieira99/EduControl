import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, interval, Subscription } from 'rxjs';
import { ApiService } from './api.service';
import { TopicItem } from '../models/models';

@Injectable({ providedIn: 'root' })
export class TimerService {

  private _elapsed   = new BehaviorSubject<number>(0);
  private _isRunning = new BehaviorSubject<boolean>(false);
  private _item      = new BehaviorSubject<TopicItem | null>(null);
  private _notes     = new BehaviorSubject<string>('');
  private _saved     = new Subject<void>();

  elapsed$   = this._elapsed.asObservable();
  isRunning$ = this._isRunning.asObservable();
  item$      = this._item.asObservable();
  notes$     = this._notes.asObservable();
  sessionSaved$ = this._saved.asObservable();

  private timerSub?: Subscription;
  private _itemId: number | null = null;

  get elapsed()  { return this._elapsed.value; }
  get isRunning(){ return this._isRunning.value; }
  get item()     { return this._item.value; }
  get notes()    { return this._notes.value; }
  get itemId()   { return this._itemId; }

  /** true quando há sessão activa (correndo OU pausada) */
  get hasActiveSession() {
    return this._itemId !== null && (this._isRunning.value || this._elapsed.value > 0);
  }

  constructor(private api: ApiService) {}

  /** Registar o item a estudar (antes de start) */
  selectItem(itemId: number, item: TopicItem) {
    this._itemId = itemId;
    this._item.next(item);
  }

  setNotes(notes: string) { this._notes.next(notes); }

  start() {
    if (!this._itemId || this._isRunning.value) return;
    this._elapsed.next(0);
    this._isRunning.next(true);
    this._tick();
  }

  pause() {
    if (!this._isRunning.value) return;
    this._isRunning.next(false);
    this.timerSub?.unsubscribe();
    this.timerSub = undefined;
  }

  resume() {
    if (this._isRunning.value || this._elapsed.value === 0) return;
    this._isRunning.next(true);
    this._tick();
  }

  /** Finalizar: salva a sessão no backend e reseta o estado */
  stop() {
    const itemId  = this._itemId;
    const elapsed = this._elapsed.value;
    const notes   = this._notes.value;

    this.timerSub?.unsubscribe();
    this.timerSub = undefined;
    this._isRunning.next(false);

    if (!itemId || elapsed === 0) { this._reset(); return; }

    const session = {
      durationSeconds: elapsed,
      notes,
      studyDate: new Date().toISOString().split('T')[0]
    };

    this.api.createSession(itemId, session as any).subscribe({
      next:  () => { this._reset(); this._saved.next(); },
      error: () => { this._reset(); this._saved.next(); }
    });
  }

  reset() {
    this.timerSub?.unsubscribe();
    this.timerSub = undefined;
    this._reset();
  }

  private _reset() {
    this._isRunning.next(false);
    this._elapsed.next(0);
    this._notes.next('');
    this._itemId = null;
    this._item.next(null);
  }

  private _tick() {
    this.timerSub = interval(1000).subscribe(() =>
      this._elapsed.next(this._elapsed.value + 1)
    );
  }
}
