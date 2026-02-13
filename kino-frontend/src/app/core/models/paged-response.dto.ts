export interface PagedResponseDto<T> {
  items: T[];
  page: number;
  totalPages: number;
  totalResults: number;
}
