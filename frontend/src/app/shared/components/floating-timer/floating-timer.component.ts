import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TimerService } from '../../../core/services/timer.service';

@Component({
  selector: 'app-floating-timer',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (timer.hasActiveSession) {
      <div class="floating-timer" [class.running]="timer.isRunning">
        <div class="ft-status">
          <span class="ft-dot" [class.pulsing]="timer.isRunning"></span>
          <span class="ft-state">{{ timer.isRunning ? 'Estudando' : 'Pausado' }}</span>
        </div>
        <div class="ft-info">
          <div class="ft-item" [title]="timer.item?.name">{{ timer.item?.name }}</div>
          <div class="ft-time">{{ displayTime }}</div>
        </div>
        <div class="ft-actions">
          @if (timer.isRunning) {
            <button class="ft-btn" (click)="timer.pause()" title="Pausar">⏸</button>
          } @else {
            <button class="ft-btn ft-btn-play" (click)="timer.resume()" title="Continuar">▶</button>
          }
          <button class="ft-btn ft-btn-stop" (click)="timer.stop()" title="Finalizar e salvar">⏹</button>
          <button class="ft-btn ft-btn-nav" (click)="goToTimer()" title="Abrir Timer">⏱</button>
        </div>
      </div>
    }
  `,
  styleUrls: ['./floating-timer.component.scss']
})
export class FloatingTimerComponent {
  constructor(public timer: TimerService, private router: Router) {}

  goToTimer() { this.router.navigate(['/timer']); }

  get displayTime(): string {
    const t = this.timer.elapsed;
    const h = Math.floor(t / 3600);
    const m = Math.floor((t % 3600) / 60);
    const s = t % 60;
    const pad = (n: number) => n.toString().padStart(2, '0');
    return h > 0 ? `${pad(h)}:${pad(m)}:${pad(s)}` : `${pad(m)}:${pad(s)}`;
  }
}
