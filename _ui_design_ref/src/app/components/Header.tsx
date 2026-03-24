import { ArrowLeft, Bell } from 'lucide-react';
import { useNavigate } from 'react-router';

interface HeaderProps {
  title: string;
  showBack?: boolean;
  showNotifications?: boolean;
  onNotificationClick?: () => void;
}

export function Header({ title, showBack = false, showNotifications = false, onNotificationClick }: HeaderProps) {
  const navigate = useNavigate();

  return (
    <header className="bg-white border-b border-gray-200 px-4 py-4 flex items-center justify-between">
      <div className="flex items-center gap-3">
        {showBack && (
          <button
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5 text-gray-700" />
          </button>
        )}
        <h1 className="text-xl font-semibold text-[#1E293B]">{title}</h1>
      </div>
      {showNotifications && (
        <button
          onClick={onNotificationClick}
          className="p-2 hover:bg-gray-100 rounded-lg transition-colors relative"
        >
          <Bell className="w-5 h-5 text-gray-700" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"></span>
        </button>
      )}
    </header>
  );
}
