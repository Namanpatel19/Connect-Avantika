import { LucideIcon } from 'lucide-react';

interface StatCardProps {
  icon: LucideIcon;
  label: string;
  value: string | number;
  trend?: string;
  color?: string;
}

export function StatCard({ icon: Icon, label, value, trend, color = '#276C84' }: StatCardProps) {
  return (
    <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
      <div className="flex items-start justify-between mb-3">
        <div
          className="p-3 rounded-lg"
          style={{ backgroundColor: `${color}15` }}
        >
          <Icon className="w-6 h-6" style={{ color }} />
        </div>
        {trend && (
          <span className="text-xs text-green-600 font-medium">{trend}</span>
        )}
      </div>
      <p className="text-2xl font-bold text-[#1E293B] mb-1">{value}</p>
      <p className="text-sm text-gray-600">{label}</p>
    </div>
  );
}
