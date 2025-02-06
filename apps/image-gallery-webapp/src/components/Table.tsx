import { ReactNode } from 'react';
import cx from 'classnames';

export interface TableProps<T extends object> {
  data: T[];
  columns: TableColumn<T>[];
}

export interface TableColumn<T extends object> {
  key?: keyof T;
  title: ReactNode;
  dataKey: keyof T;
  className?: string;
}

export function Table<T extends object>({ data, columns }: TableProps<T>) {
  return (
    <table className="relative mb-6 w-[100%] overflow-x-auto">
      <thead className="bg-neutral-300 text-sm text-neutral-700 dark:bg-neutral-700 dark:text-neutral-400">
        <tr>
          {columns.map((column, index) => (
            <th key={index} scope="col" className={cx('px-4 py-1 text-start', column.className)}>
              {column.title}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map((row, index) => (
          <tr
            key={index}
            className={cx('dark:bg-neutral-800', {
              'border-b border-neutral-300 dark:border-neutral-700': index !== data.length - 1,
            })}>
            {columns.map((column) => (
              <td
                key={getKey(row, column)}
                style={{ wordBreak: 'break-word' }}
                className={cx('px-4 py-1 text-sm dark:text-neutral-100', column.className)}>
                {getValue(row, column)}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function getKey<T extends object>(data: T, column: TableColumn<T>) {
  const key = column.key ?? column.dataKey;
  return String(data[key]);
}

function getValue<T extends object>(data: T, column: TableColumn<T>) {
  return String(data[column.dataKey]);
}
