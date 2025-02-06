import { Link as RouterLink } from '@tanstack/react-router';

export type LinkProps = Parameters<typeof RouterLink>[0] & {
  size?: keyof typeof SIZES;
  type?: keyof typeof TYPES;
};

export function Link({ size, type, ...props }: LinkProps) {
  const className = [
    'flex select-none items-center',
    TYPES[type ?? 'primary'],
    type != 'text' && SIZES[size ?? 'md'],
    props.className,
  ]
    .filter(Boolean)
    .join(' ');
  return <RouterLink {...props} className={className} />;
}

const SIZES = {
  md: 'text-md rounded-md px-5',
} as const;

const TYPES = {
  text: '',
  primary: 'bg-blue-600 text-neutral-50 hover:bg-blue-500',
  secondary: 'text-blue-600 hover:text-blue-500',
} as const;
