import { useNavigate } from 'react-router';
import { Shield, Users, BookOpen, UserCircle } from 'lucide-react';
import { MobileContainer } from '../components/MobileContainer';

export function RoleSelectionScreen() {
  const navigate = useNavigate();

  const roles = [
    {
      id: 'admin',
      title: 'Admin',
      description: 'Dean / Student Office',
      icon: Shield,
      color: '#276C84',
      path: '/admin/dashboard',
    },
    {
      id: 'club-lead',
      title: 'Club Lead',
      description: 'Head of Student Club',
      icon: Users,
      color: '#8B5CF6',
      path: '/club-lead/dashboard',
    },
    {
      id: 'faculty',
      title: 'Faculty',
      description: 'Teachers & Professors',
      icon: BookOpen,
      color: '#F59E0B',
      path: '/faculty/dashboard',
    },
    {
      id: 'student',
      title: 'Student',
      description: 'University Student',
      icon: UserCircle,
      color: '#22C55E',
      path: '/student/dashboard',
    },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] px-6 py-12">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-[#1E293B] mb-2">
            Select Your Role
          </h1>
          <p className="text-gray-600">
            Choose how you want to access the system
          </p>
        </div>

        <div className="space-y-4">
          {roles.map((role) => {
            const Icon = role.icon;
            return (
              <button
                key={role.id}
                onClick={() => navigate(role.path)}
                className="w-full bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md hover:border-gray-200 transition-all group"
              >
                <div className="flex items-center gap-4">
                  <div
                    className="p-4 rounded-xl transition-transform group-hover:scale-110"
                    style={{ backgroundColor: `${role.color}15` }}
                  >
                    <Icon className="w-8 h-8" style={{ color: role.color }} />
                  </div>
                  <div className="text-left flex-1">
                    <h3 className="text-lg font-semibold text-[#1E293B] mb-1">
                      {role.title}
                    </h3>
                    <p className="text-sm text-gray-600">{role.description}</p>
                  </div>
                  <svg
                    className="w-6 h-6 text-gray-400 group-hover:text-gray-600 transition-colors"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                </div>
              </button>
            );
          })}
        </div>

        <div className="mt-8 text-center">
          <button
            onClick={() => navigate('/login')}
            className="text-gray-600 hover:text-gray-800 font-medium text-sm"
          >
            ← Back to Login
          </button>
        </div>
      </div>
    </MobileContainer>
  );
}
