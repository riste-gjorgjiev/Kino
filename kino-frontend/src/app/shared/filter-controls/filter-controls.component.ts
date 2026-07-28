import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FilterState } from '../../core/models/filter-state.model';

@Component({
  selector: 'app-filter-controls',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './filter-controls.component.html',
  styleUrl: './filter-controls.component.css',
})
export class FilterControlsComponent {
  @Input() filter: FilterState = { yearFrom: null, yearTo: null, sortBy: null, sortOrder: "asc" };
  @Output() filterChanged = new EventEmitter<FilterState>();

  onYearFromChange(value: string): void {
    const num = value === '' ? null : Number(value);
    this.filter = { ...this.filter, yearFrom: num }
    this.filterChanged.emit(this.filter);
  }

  onYearToChange(value: string): void {
    const num = value === '' ? null : Number(value);
    this.filter = { ...this.filter, yearTo: num };
    this.filterChanged.emit(this.filter);
  }

  onSortByChange(value: string): void {
    const sortBy = value === '' ? null : value;
    this.filter = { ...this.filter, sortBy };
    this.filterChanged.emit(this.filter);
  }

  onSortOrderChange(value: 'asc' | 'desc'): void {
    this.filter = { ...this.filter, sortOrder: value };
    this.filterChanged.emit(this.filter);
  }

  clear(): void {
    this.filter = { yearFrom:null, yearTo:null, sortBy: null, sortOrder:'asc' };
    this.filterChanged.emit(this.filter);
  }

}
