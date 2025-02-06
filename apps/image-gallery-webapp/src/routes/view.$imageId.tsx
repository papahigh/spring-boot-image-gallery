import { createFileRoute, notFound } from '@tanstack/react-router';
import cx from 'classnames';
import { z } from 'zod';

import { Button } from '~/components/Button';
import { Picture } from '~/components/Picture';
import { Content, Section } from '~/components/Layout';
import { Location } from '~/components/Location';
import { Table, TableColumn } from '~/components/Table';

import { downloadImage, isNotFoundError } from '~/utils/axiosClient';
import { imageQueryOptions } from '~/utils/queryClient';

import type { Classification, ImageDetails, MetaTag } from '~/types';

export const Route = createFileRoute('/view/$imageId')({
  component: RouteComponent,
  loader: async ({ context, params, route }) => {
    try {
      return await context.queryClient.ensureQueryData(imageQueryOptions(params.imageId));
    } catch (error) {
      if (isNotFoundError(error)) {
        return notFound();
      } else {
        route.router?.navigate({ to: '/' });
      }
    }
  },
  params: {
    parse: ({ imageId }) => ({
      imageId: z.string().parse(imageId),
    }),
    stringify: ({ imageId }) => ({ imageId }),
  },
});

function RouteComponent() {
  const data = Route.useLoaderData() as ImageDetails;
  return (
    <>
      <div
        className={cx(
          'lg:y-12 py-6',
          'flex flex-col items-center',
          'bg-neutral-50 dark:bg-black',
          'shadow-neutral-250 shadow-sm dark:shadow-neutral-900',
        )}>
        <Picture bundle={data.fullSize} className={`max-h-[calc(100vh-320px)] max-w-full`} />
      </div>
      <Content>
        <div className="mb-12 flex flex-wrap justify-between gap-4">
          <h1 className="text-2xl">{data.originalFile.fileName}</h1>
          <Button type="secondary" onClick={() => downloadImage(data)}>
            Download
          </Button>
        </div>
        {data.location && (
          <Section title="Location">
            <Location location={data.location} />
          </Section>
        )}
        {data.classes.length > 0 && (
          <Section title="Classification">
            <Table columns={CLASSIFICATION_COLUMNS} data={data.classes} />
          </Section>
        )}
        {data.metadata.length > 0 && (
          <Section title="Metadata">
            <Table columns={MATADATA_COLUMNS} data={data.metadata} />
          </Section>
        )}
      </Content>
    </>
  );
}

const CLASSIFICATION_COLUMNS = [
  {
    title: 'Class Name',
    dataKey: 'className',
  },
  {
    title: 'Probability',
    dataKey: 'probability',
  },
] as const satisfies TableColumn<Classification>[];

const MATADATA_COLUMNS = [
  {
    title: 'Directory',
    dataKey: 'directory',
    className: 'w-[20%]',
  },
  {
    title: 'Tag Name',
    dataKey: 'tagName',
    className: 'w-[25%]',
  },
  {
    title: 'Tag Value',
    dataKey: 'tagValue',
  },
] as const satisfies TableColumn<MetaTag>[];
