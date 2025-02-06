import { createFileRoute, Outlet } from '@tanstack/react-router';

export const Route = createFileRoute('/view')({
  component: Outlet,
  notFoundComponent: () => <p>Not Found</p>,
});
