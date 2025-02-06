import { TbLoader2 as Icon } from 'react-icons/tb';

export interface SpinnerProps {
  show?: boolean;
  wait?: `delay-${number}`;
}

export function Spinner({ show, wait }: SpinnerProps) {
  const className = [
    show !== false && 'animate-spin transition',
    show !== false && `opacity-1 duration-500 ${wait ?? 'delay-300'}`,
    show === false && `opacity-0 delay-0 duration-500`,
  ]
    .filter(Boolean)
    .join(' ');
  return (
    <div className={className}>
      <Icon />
    </div>
  );
}
