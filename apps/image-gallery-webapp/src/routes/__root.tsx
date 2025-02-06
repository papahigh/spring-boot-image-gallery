import { createRootRouteWithContext, Outlet, useRouterState } from '@tanstack/react-router';
import type { QueryClient } from '@tanstack/react-query';

import { TanStackRouterDevtools } from '@tanstack/router-devtools';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

import { Spinner } from '~/components/Spinner';
import { Header } from '~/components/Layout';
import { Link } from '~/components/Link';

function RouterSpinner() {
  const isLoading = useRouterState({ select: (s) => s.status === 'pending' });
  return <Spinner show={isLoading} />;
}

export const Route = createRootRouteWithContext<{
  queryClient: QueryClient;
}>()({
  component: RootComponent,
});

function RootComponent() {
  return (
    <>
      <div className="w-max-screen flex min-h-screen flex-col">
        <Header>
          <div className={`flex items-center gap-2`}>
            <Link to="/" type="text" className={`text-2xl`}>
              Image Gallery
            </Link>
            <div className={`text-2xl`}>
              <RouterSpinner />
            </div>
          </div>
          <Link type="primary" to="/upload">
            Upload
          </Link>
        </Header>
        <Outlet />
      </div>
      <ReactQueryDevtools buttonPosition="bottom-left" />
      <TanStackRouterDevtools position="bottom-right" />
    </>
  );
}
