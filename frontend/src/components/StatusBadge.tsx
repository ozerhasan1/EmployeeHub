import type { EmploymentStatus } from '../types/employee';

const styles: Record<EmploymentStatus, string> = {
  ACTIVE: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
  ON_LEAVE: 'bg-amber-50 text-amber-800 ring-amber-200',
  TERMINATED: 'bg-slate-100 text-slate-600 ring-slate-300',
};

const labels: Record<EmploymentStatus, string> = {
  ACTIVE: 'Active',
  ON_LEAVE: 'On leave',
  TERMINATED: 'Terminated',
};

interface StatusBadgeProps {
  status: EmploymentStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return (
    <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ring-1 ${styles[status]}`}>
      {labels[status]}
    </span>
  );
}
