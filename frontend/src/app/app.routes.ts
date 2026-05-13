import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'subjects',
    loadComponent: () => import('./features/subjects/subjects.component').then(m => m.SubjectsComponent)
  },
  {
    path: 'planner',
    loadComponent: () => import('./features/planner/planner.component').then(m => m.PlannerComponent)
  },
  {
    path: 'timer',
    loadComponent: () => import('./features/timer/timer.component').then(m => m.TimerComponent)
  },
  {
    path: 'library',
    loadComponent: () => import('./features/library/library.component').then(m => m.LibraryComponent)
  },
  {
    path: 'notes',
    loadComponent: () => import('./features/notes/notes.component').then(m => m.NotesComponent)
  },
  {
    path: 'analytics',
    loadComponent: () => import('./features/analytics/analytics.component').then(m => m.AnalyticsComponent)
  },
  {
    path: 'ai',
    loadComponent: () => import('./features/ai-assistant/ai-assistant.component').then(m => m.AiAssistantComponent)
  },
  { path: '**', redirectTo: 'dashboard' }
];
