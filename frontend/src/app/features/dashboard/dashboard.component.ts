import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { DashboardData, RecentActivity, DailyStat } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  data: DashboardData | null = null;
  loading = true;
  today = new Date();

  backupLoading = false;
  backupMessage: string | null = null;
  backupError = false;

  dayNames = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  todayDayName = this.dayNames[this.today.getDay()];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.api.getDashboard().subscribe({
      next: d => { this.data = d; this.loading = false; },
      error: () => this.loading = false
    });
  }

  exportBackup() {
    this.backupLoading = true;
    this.backupMessage = null;
    this.api.exportBackup().subscribe({
      next: res => {
        this.backupLoading = false;
        this.backupError = false;
        this.backupMessage = `✅ Exportado: ${res.filename}`;
        setTimeout(() => this.backupMessage = null, 5000);
      },
      error: err => {
        this.backupLoading = false;
        this.backupError = true;
        this.backupMessage = `❌ Erro ao exportar: ${err.error?.error || err.message}`;
        setTimeout(() => this.backupMessage = null, 6000);
      }
    });
  }

  importBackup() {
    if (!confirm('Restaurar o snapshot mais recente? Os dados atuais serão substituídos.')) return;
    this.backupLoading = true;
    this.backupMessage = null;
    this.api.importBackup().subscribe({
      next: res => {
        this.backupLoading = false;
        this.backupError = false;
        this.backupMessage = `✅ Restaurado: ${res.filename}`;
        setTimeout(() => this.backupMessage = null, 5000);
        this.load();
      },
      error: err => {
        this.backupLoading = false;
        this.backupError = true;
        this.backupMessage = `❌ Erro ao importar: ${err.error?.error || err.message}`;
        setTimeout(() => this.backupMessage = null, 6000);
      }
    });
  }

  formatTime(seconds: number): string {
    if (!seconds) return '0h 0m';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h === 0) return `${m}m`;
    return `${h}h ${m}m`;
  }

  formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
  }

  getBarHeight(stat: DailyStat): number {
    if (!this.data?.weeklyStats?.length) return 0;
    const max = Math.max(...this.data.weeklyStats.map(s => s.durationSeconds), 1);
    return Math.round((stat.durationSeconds / max) * 100);
  }

  getDayShort(dateStr: string): string {
    const d = new Date(dateStr + 'T00:00:00');
    return this.dayNames[d.getDay()];
  }

  getTotalPct(subjectSeconds: number): number {
    if (!this.data?.subjectStats?.length) return 0;
    const total = this.data.subjectStats.reduce((a, s) => a + s.totalSeconds, 0);
    return total > 0 ? Math.round((subjectSeconds / total) * 100) : 0;
  }
}
