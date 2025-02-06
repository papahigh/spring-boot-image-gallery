import { Link } from '@tanstack/react-router';

import { Picture } from './Picture';
import { ImageSummary } from '~/types';

export interface ImageCardProps {
  image: ImageSummary;
}

export function ImageCard({ image }: ImageCardProps) {
  return (
    <Link
      viewTransition
      to="/view/$imageId"
      params={{ imageId: image.id }}
      className="h-auto max-w-full overflow-hidden rounded-lg bg-neutral-100 shadow-sm dark:bg-neutral-900">
      <Picture
        bundle={image.thumbnail}
        className="fit-cover h-[400px] w-full object-cover md:h-[300px]"
      />
      <p className="text-balance break-words p-3 text-sm">{image.fileName}</p>
    </Link>
  );
}
