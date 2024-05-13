export interface PageDto<T> {
  content: T[];
  page: Page;
}

interface Page {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}
