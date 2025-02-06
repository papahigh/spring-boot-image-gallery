import { Page } from '~/utils/axiosClient';
import { Button } from '~/components/Button';

interface PaginationProps {
  page: Page;
  goTo: (page: number) => void;
}

export function Pagination({ page, goTo }: PaginationProps) {
  const hasNext = page.number < page.totalPages - 1;
  const hasPrev = page.number > 0;

  const getPrev = () => goTo(page.number - 1);
  const getNext = () => goTo(page.number + 1);

  return (
    <div className="flex justify-center gap-3 pb-6 pt-12">
      <Button type="secondary" size="lg" disabled={!hasPrev} onClick={getPrev}>
        Prev
      </Button>
      <Button type="secondary" size="lg" disabled={!hasNext} onClick={getNext}>
        Next
      </Button>
    </div>
  );
}
