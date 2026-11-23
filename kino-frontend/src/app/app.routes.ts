import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then(m => m.Home),
  },
  {
    path: 'movies/:category',
    loadComponent: () => import('./pages/media-list-page/media-list-page').then(m => m.MediaListPage),
  },
  {
    path: 'tv/:category',
    loadComponent: () => import('./pages/media-list-page/media-list-page').then(m => m.MediaListPage),
  },
  {
    path: 'movies/details/:id',
    loadComponent: () => import('./pages/details/details').then(m => m.Details),
  },
  {
    path: 'tv/details/:id',
    loadComponent: () => import('./pages/details/details').then(m => m.Details),
  },
  {
    path: 'person/:id',
    loadComponent: () => import('./pages/person/person').then(m => m.Person),
  },
  {
    path: 'search',
    loadComponent: () => import('./pages/search/search').then(m => m.Search),
  },
  { path: '**', redirectTo: '' },
];
