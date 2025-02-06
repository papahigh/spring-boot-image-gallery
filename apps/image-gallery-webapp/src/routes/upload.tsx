import React, { useEffect } from 'react';
import { IoImageOutline } from 'react-icons/io5';
import cx from 'classnames';

import { createFileRoute } from '@tanstack/react-router';

import { Content } from '~/components/Layout';
import { useImageUpload } from '~/utils/queryClient';

export const Route = createFileRoute('/upload')({
  component: RouteComponent,
});

function RouteComponent() {
  const { mutate, data, isSuccess, isPending } = useImageUpload();
  const navigate = Route.useNavigate();

  useEffect(() => {
    if (isSuccess) {
      navigate({ to: '/view/$imageId', params: { imageId: data?.id } });
    }
  }, [isSuccess, data, navigate]);

  const ref = React.useRef<HTMLFormElement | null>(null);

  function onChange() {
    ref.current?.requestSubmit();
  }

  return (
    <Content>
      <div className="flex flex-col items-center">
        <h1 className="py-12 text-4xl">Upload an Image</h1>
        <form action={mutate} ref={ref} className={'w-full'}>
          <label
            className={cx(
              'cursor-pointer',
              'flex flex-col items-center justify-center',
              'h-[400px] w-[100%]',
              'text-neutral-300 dark:text-neutral-600',
              'rounded-lg border-2 border-dashed border-neutral-300 hover:border-blue-600 dark:border-neutral-600',
            )}>
            <IoImageOutline size={160} />
            <input
              type="file"
              name="file"
              className="hidden"
              disabled={isPending}
              accept=".jpg,.webp,.avif,.tiff,.png"
              onChange={onChange}
            />
            <p className="text-balance px-10 text-center text-neutral-500 dark:text-neutral-400">
              Please upload an image file in one of the following formats: JPG, WEBP, AVIF, TIFF, or
              PNG.
            </p>
          </label>
        </form>
      </div>
    </Content>
  );
}
