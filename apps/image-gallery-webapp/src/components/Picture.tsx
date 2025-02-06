import { ImageBundle } from '~/types';

export interface PictureProps {
  bundle: ImageBundle;
  className?: string;
}

export function Picture({ bundle, className }: PictureProps) {
  return (
    <picture>
      {bundle.avif && <source srcSet={bundle.avif} />}
      {bundle.webp && <source srcSet={bundle.webp} />}
      {bundle.jpeg && <img src={bundle.jpeg} alt="picture" className={className} />}
    </picture>
  );
}
