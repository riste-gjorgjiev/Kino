import { Routes } from '@angular/router';
import { Home } from './pages/home/home'
import { MediaListPage } from './pages/media-list-page/media-list-page';
import { Details } from './pages/details/details';
import { Search } from './pages/search/search'

export const routes: Routes = [
  {path: '', component: Home},
  {path: 'movies/:category', component: MediaListPage},
  {path: 'tv/:category', component: MediaListPage},
  {path: 'movies/details/:id', component: Details},
  {path: 'tv/details/:id', component: Details},
  {path: 'search', component: Search},

  {path: '**', redirectTo: ''},
];
