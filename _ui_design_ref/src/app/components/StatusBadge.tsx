interface StatusBadgeProps {
  status: 'pending' | 'approved' | 'rejected' | 'accepted';
  size?: 'sm' | 'md';
}

export function StatusBadge({ status, size = 'sm' }: StatusBadgeProps) {
  const statusConfig = {
    pending: { bg: 'bg-yellow-100', text: 'text-yellow-700', label: 'Pending' },
    approved: { bg: 'bg-green-100', text: 'text-green-700', label: 'Approved' },
    rejected: { bg: 'bg-red-100', text: 'text-red-700', label: 'Rejected' },
    accepted: { bg: 'bg-green-100', text: 'text-green-700', label: 'Accepted' },
  };

  const config = statusConfig[status];
  const sizeClasses = size === 'sm' ? 'px-2 py-1 text-xs' : 'px-3 py-1.5 text-sm';

  return (
    <span className={`${config.bg} ${config.text} ${sizeClasses} rounded-full font-medium inline-block`}>
      {config.label}
    </span>
  );
}
