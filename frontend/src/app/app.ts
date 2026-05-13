import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ApiService } from './core/services/api.service';

interface NavItem { path: string; icon: SafeHtml; label: string; }

const S = (d: string) =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;

const ICONS: Record<string, string> = {
  dashboard: S(`<rect x="2" y="2" width="9" height="9" rx="1.5"/><rect x="13" y="2" width="9" height="9" rx="1.5"/><rect x="2" y="13" width="9" height="9" rx="1.5"/><rect x="13" y="13" width="9" height="9" rx="1.5"/>`),
  subjects:  S(`<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/><line x1="9" y1="7" x2="15" y2="7"/><line x1="9" y1="11" x2="13" y2="11"/>`),
  planner:   S(`<rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/>`),
  timer:     S(`<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2.5 2.5M9.5 2.5h5"/>`),
  library:   S(`<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/>`),
  notes:     S(`<path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4z"/>`),
  analytics: S(`<line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>`),
  ai:        S(`<path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/><path d="M8 10h8M8 14h4"/>`),
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  title = 'EduControl';
  sidebarOpen = false;
  navItems: NavItem[];

  initializing = true;
  splashFading = false;
  initStep: 'connecting' | 'importing' | 'done' = 'connecting';

  constructor(private sanitizer: DomSanitizer, private api: ApiService) {
    const safe = (k: string) => sanitizer.bypassSecurityTrustHtml(ICONS[k]);
    this.navItems = [
      { path: '/dashboard', icon: safe('dashboard'), label: 'Dashboard'  },
      { path: '/subjects',  icon: safe('subjects'),  label: 'Matérias'   },
      { path: '/planner',   icon: safe('planner'),   label: 'Planner'    },
      { path: '/timer',     icon: safe('timer'),     label: 'Timer'      },
      { path: '/library',   icon: safe('library'),   label: 'Biblioteca' },
      { path: '/notes',     icon: safe('notes'),     label: 'Anotações'  },
      { path: '/analytics', icon: safe('analytics'), label: 'Análises'   },
      { path: '/ai',        icon: safe('ai'),        label: 'IA'         },
    ];
  }

  ngOnInit() {
    setTimeout(() => {
      this.initStep = 'importing';
      this.api.importBackup().subscribe({
        next: () => this.finishSplash(),
        error: () => this.finishSplash()
      });
    }, 1200);
  }

  private finishSplash() {
    this.initStep = 'done';
    setTimeout(() => {
      this.splashFading = true;
      setTimeout(() => { this.initializing = false; }, 600);
    }, 700);
  }

  toggleSidebar() { this.sidebarOpen = !this.sidebarOpen; }
}

