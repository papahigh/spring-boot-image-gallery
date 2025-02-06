import type { HTMLAttributes, PropsWithChildren } from 'react';
import cx from 'classnames';

export function Header({ children, className }: PropsWithChildren<HTMLAttributes<HTMLDivElement>>) {
  return (
    <div
      className={cx(
        'flex justify-between',
        'px-5 py-3 lg:px-7',
        'bg-neutral-50 dark:bg-neutral-900',
        'shadow-neutral-250 shadow-sm dark:shadow-neutral-900',
        className,
      )}>
      {children}
    </div>
  );
}

export function Content({ children }: PropsWithChildren) {
  return (
    <div className="flex flex-col items-center">
      <div className="w-full p-5 lg:p-10 xl:max-w-[1200px]">{children}</div>
    </div>
  );
}

export function Grid({ children }: PropsWithChildren) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
      {children}
    </div>
  );
}

export function Section({ title, children }: PropsWithChildren<{ title: string }>) {
  return (
    <section className="mb-6 md:mb-12">
      <p className="mb-4 text-lg">{title}</p>
      {children}
    </section>
  );
}
