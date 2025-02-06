import { HTMLProps } from 'react';

export interface ButtonProps extends Omit<HTMLProps<HTMLButtonElement>, 'size'> {
  size?: keyof typeof SIZES;
  type?: keyof typeof TYPES;
}

export function Button({ size, type, ...props }: ButtonProps) {
  const className = [
    'flex select-none items-center',
    'disabled:opacity-50 disabled:cursor-not-allowed',
    SIZES[size || 'md'],
    TYPES[type || 'primary'],
    props.className,
  ]
    .filter(Boolean)
    .join(' ');
  return <button {...props} className={className} />;
}

const SIZES = {
  md: 'text-md rounded-md px-6 h-[32px]',
  lg: 'text-lg rounded-md px-8 h-[38px]',
} as const;

const TYPES = {
  text: '',
  primary: 'bg-blue-600 text-neutral-50 hover:bg-blue-500',
  secondary: 'border border-blue-600 hover:border-blue-500',
} as const;
