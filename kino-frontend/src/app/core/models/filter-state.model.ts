export interface FilterState {
  yearFrom: number | null;
  yearTo: number | null;
  sortBy: string | null;
  sortOrder: 'asc' | 'desc';
}

export const DEFAULT_FILTER: FilterState = {
  yearFrom: null,
  yearTo: null,
  sortBy: null,
  sortOrder: 'asc'
}
