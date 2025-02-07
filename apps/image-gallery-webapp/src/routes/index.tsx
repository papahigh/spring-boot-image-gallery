import { useEffect } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import { useSuspenseQuery } from '@tanstack/react-query';
import { z } from 'zod';

import { Content, Grid } from '~/components/Layout';
import { ImageCard } from '~/components/ImageCard';
import { Pagination } from '~/components/Pagination';

import { imagesQueryOptions } from '~/utils/queryClient';

export const Route = createFileRoute('/')({
  component: IndexComponent,
  loaderDeps: ({ search }) => search,
  loader: ({ context, deps }) => context.queryClient.ensureQueryData(imagesQueryOptions(deps)),
  validateSearch: z.object({
    page: z.number().gte(0).catch(0),
    size: z.number().gte(1).catch(24),
  }),
});

function IndexComponent() {
  const search = Route.useSearch();
  const navigate = Route.useNavigate();
  const query = useSuspenseQuery(imagesQueryOptions(search));

  const goToPage = (page: number) =>
    navigate({
      to: Route.path,
      search: (prev) => ({ ...prev, page }),
      resetScroll: false,
      viewTransition: true,
    });

  useEffect(() => {
    if (query.isFetched && query.data.page.totalElements == 0) {
      navigate({ to: '/upload' });
    }
  }, [query.data, query.isFetched, navigate]);

  return (
    <Content>
      <Grid>{query.data?.content?.map((image) => <ImageCard key={image.id} image={image} />)}</Grid>
      {query?.data.page.totalPages > 1 && <Pagination page={query.data.page} goTo={goToPage} />}
    </Content>
  );
}
