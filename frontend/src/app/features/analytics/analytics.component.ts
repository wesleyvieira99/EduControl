import { Component, OnInit, AfterViewInit, OnDestroy, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { DashboardData, CalendarEvent } from '../../core/models/models';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('subjectChart') subjectChartRef!: ElementRef<HTMLCanvasElement>;

  dashboard: DashboardData | null = null;
  calendarData: CalendarEvent[] = [];
  calendarWeeks: CalendarEvent[][] = [];
  loading = true;

  private chart?: Chart;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getDashboard().subscribe({ next: d => { this.dashboard = d; this.loading = false; }, error: () => this.loading = false });
    const to = new Date();
    const from = new Date(); from.setMonth(from.getMonth() - 11); from.setDate(1);
    this.api.getCalendarData(from.toISOString().split('T')[0], to.toISOString().split('T')[0])
      .subscribe(data => { this.calendarData = data; this.buildCalendar(data); });
  }

  ngAfterViewInit() {
    if (this.dashboard) this.buildChart();
  }

  ngOnDestroy() { this.chart?.destroy(); }

  buildCalendar(data: CalendarEvent[]) {
    const map = new Map<string, CalendarEvent>();
    data.forEach(d => map.set(d.date, d));

    const today = new Date();
    const start = new Date(); start.setMonth(start.getMonth() - 11); start.setDate(1);
    // Align to Sunday
    const startDay = new Date(start);
    startDay.setDate(startDay.getDate() - startDay.getDay());

    const weeks: CalendarEvent[][] = [];
    let cur = new Date(startDay);
    while (cur <= today) {
      const week: CalendarEvent[] = [];
      for (let d = 0; d < 7; d++) {
        const key = cur.toISOString().split('T')[0];
        const existing = map.get(key);
        week.push(existing || { date: key, totalSeconds: 0, sessionsCount: 0, level: 'none' });
        cur = new Date(cur); cur.setDate(cur.getDate() + 1);
      }
      weeks.push(week);
    }
    this.calendarWeeks = weeks;
  }

  buildChart() {
    if (!this.subjectChartRef || !this.dashboard?.subjectStats?.length) return;
    this.chart?.destroy();
    const labels = this.dashboard.subjectStats.map(s => s.subjectName);
    const data = this.dashboard.subjectStats.map(s => Math.round(s.totalSeconds / 60));
    this.chart = new Chart(this.subjectChartRef.nativeElement, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: this.dashboard.subjectStats.map((s,i) => s.color || ['#6366f1','#8b5cf6','#ec4899','#3b82f6','#22c55e'][i % 5]),
          borderWidth: 2,
          borderColor: 'rgba(255,255,255,0.1)'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { labels: { color: '#94a3b8', font: { size: 12 } } } },
        cutout: '65%'
      }
    });
  }

  getLevelColor(level: string): string {
    return { none: 'transparent', low: '#312e81', medium: '#6366f1', high: '#a5b4fc' }[level] || 'transparent';
  }

  formatTime(s: number): string {
    if (!s) return '—';
    const h = Math.floor(s / 3600); const m = Math.floor((s % 3600) / 60);
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  get maxDaySeconds(): number {
    if (!this.dashboard?.weeklyStats?.length) return 1;
    return Math.max(...this.dashboard.weeklyStats.map(d => d.durationSeconds), 1);
  }

  getTooltip(ev: CalendarEvent): string {
    if (!ev.totalSeconds) return ev.date;
    return `${ev.date}: ${this.formatTime(ev.totalSeconds)} (${ev.sessionsCount} sessões)`;
  }

  // called after dashboard loads
  ngAfterViewChecked_once = false;
  ngAfterViewChecked() {
    if (!this.ngAfterViewChecked_once && this.dashboard && this.subjectChartRef) {
      this.ngAfterViewChecked_once = true;
      setTimeout(() => this.buildChart(), 100);
    }
  }
}
