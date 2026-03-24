import { ReactNode } from 'react';

interface MobileContainerProps {
  children: ReactNode;
  className?: string;
}

export function MobileContainer({ children, className = '' }: MobileContainerProps) {
  return (
    <div className="min-h-screen bg-[#F8FAFC] flex items-center justify-center p-4">
      <div className={`w-full max-w-[414px] min-h-screen bg-[#F8FAFC] ${className}`}>
        {children}
      </div>
    </div>
  );
}
