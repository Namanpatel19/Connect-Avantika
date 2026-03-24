import { Home, Calendar, Users, User } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router';

interface BottomNavProps {
  role: 'admin' | 'club-lead' | 'faculty' | 'student';
}

export function BottomNav({ role }: BottomNavProps) {
  const navigate = useNavigate();
  const location = useLocation();

  const getNavItems = () => {
    if (role === 'student') {
      return [
        { icon: Home, label: 'Home', path: '/student/dashboard' },
        { icon: Calendar, label: 'Calendar', path: '/student/calendar' },
        { icon: Users, label: 'Clubs', path: '/student/clubs' },
        { icon: User, label: 'Profile', path: '/student/profile' },
      ];
    } else if (role === 'admin') {
      return [
        { icon: Home, label: 'Home', path: '/admin/dashboard' },
        { icon: Users, label: 'Students', path: '/admin/students' },
        { icon: Calendar, label: 'Events', path: '/admin/event-approvals' },
        { icon: User, label: 'Profile', path: '/admin/profile' },
      ];
    } else if (role === 'club-lead') {
      return [
        { icon: Home, label: 'Home', path: '/club-lead/dashboard' },
        { icon: Calendar, label: 'Calendar', path: '/club-lead/calendar' },
        { icon: Users, label: 'Members', path: '/club-lead/member-requests' },
        { icon: User, label: 'Profile', path: '/club-lead/profile' },
      ];
    } else if (role === 'faculty') {
      return [
        { icon: Home, label: 'Home', path: '/faculty/dashboard' },
        { icon: Users, label: 'Students', path: '/faculty/student-management' },
        { icon: Calendar, label: 'Events', path: '/faculty/events-clubs' },
        { icon: User, label: 'Profile', path: '/faculty/profile' },
      ];
    }
    return [];
  };

  const navItems = getNavItems();

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3 flex items-center justify-around max-w-[414px] mx-auto">
      {navItems.map((item) => {
        const Icon = item.icon;
        const isActive = location.pathname === item.path;
        return (
          <button
            key={item.path}
            onClick={() => navigate(item.path)}
            className="flex flex-col items-center gap-1 flex-1"
          >
            <Icon
              className={`w-6 h-6 ${
                isActive ? 'text-[#276C84]' : 'text-gray-400'
              }`}
            />
            <span
              className={`text-xs ${
                isActive ? 'text-[#276C84] font-medium' : 'text-gray-400'
              }`}
            >
              {item.label}
            </span>
          </button>
        );
      })}
    </nav>
  );
}