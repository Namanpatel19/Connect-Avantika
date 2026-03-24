import { LucideIcon } from 'lucide-react';

interface ActionButtonProps {
  icon: LucideIcon;
  label: string;
  onClick: () => void;
  variant?: 'primary' | 'secondary';
}

export function ActionButton({ icon: Icon, label, onClick, variant = 'primary' }: ActionButtonProps) {
  const baseClasses = "flex items-center gap-3 px-4 py-3 rounded-xl font-medium transition-all";
  const variantClasses = variant === 'primary'
    ? "bg-[#276C84] text-white hover:bg-[#1f5668] shadow-sm"
    : "bg-white text-[#276C84] border border-[#276C84] hover:bg-[#276C84] hover:text-white";

  return (
    <button
      onClick={onClick}
      className={`${baseClasses} ${variantClasses}`}
    >
      <Icon className="w-5 h-5" />
      <span>{label}</span>
    </button>
  );
}
