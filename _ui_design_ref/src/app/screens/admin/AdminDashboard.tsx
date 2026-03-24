import { useNavigate } from 'react-router';
import { Users, BookOpen, Calendar, UserPlus, Megaphone, CheckCircle, FileText, Settings, User, Bell, TrendingUp } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { BottomNav } from '../../components/BottomNav';

export function AdminDashboard() {
  const navigate = useNavigate();

  const stats = [
    { label: 'Total Students', value: '2,845', icon: Users, color: 'bg-blue-100 text-blue-600' },
    { label: 'Total Faculty', value: '142', icon: BookOpen, color: 'bg-purple-100 text-purple-600' },
    { label: 'Active Clubs', value: '28', icon: Users, color: 'bg-green-100 text-green-600' },
    { label: 'Total Events', value: '64', icon: Calendar, color: 'bg-orange-100 text-orange-600' },
  ];

  const quickActions = [
    { icon: UserPlus, label: 'Manage Students', route: '/admin/students', color: 'bg-blue-100 text-blue-600', description: 'View & edit' },
    { icon: BookOpen, label: 'Manage Faculty', route: '/admin/faculty', color: 'bg-purple-100 text-purple-600', description: 'Faculty list' },
    { icon: CheckCircle, label: 'Approve Events', route: '/admin/event-approvals', color: 'bg-green-100 text-green-600', description: '3 pending', count: 3 },
    { icon: Megaphone, label: 'Announcements', route: '/admin/announcements', color: 'bg-orange-100 text-orange-600', description: 'Make new' },
    { icon: FileText, label: 'Reports', route: '/admin/reports', color: 'bg-pink-100 text-pink-600', description: 'View analytics' },
    { icon: Settings, label: 'Settings', route: '/admin/settings', color: 'bg-gray-100 text-gray-600', description: 'System config' },
  ];

  const recentActivities = [
    { id: 1, title: 'New event request from Tech Club', time: '2 hours ago', type: 'event', color: 'bg-green-500' },
    { id: 2, title: '45 new student registrations', time: '5 hours ago', type: 'students', color: 'bg-blue-500' },
    { id: 3, title: 'Faculty meeting scheduled', time: '1 day ago', type: 'meeting', color: 'bg-purple-500' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        {/* Custom Header with Profile */}
        <div className="bg-gradient-to-b from-[#276C84] to-[#1f5668] px-4 pt-12 pb-24 rounded-b-[2rem]">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
                <User className="w-6 h-6 text-white" />
              </div>
              <div>
                <p className="text-white/80 text-sm">Welcome back,</p>
                <h2 className="text-white text-xl font-bold">Dean Anderson</h2>
              </div>
            </div>
            <button 
              onClick={() => navigate('/admin/notifications')}
              className="bg-white/20 backdrop-blur-sm p-2.5 rounded-full relative"
            >
              <Bell className="w-5 h-5 text-white" />
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-semibold">
                5
              </span>
            </button>
          </div>
        </div>

        <div className="px-4 -mt-16 space-y-6 pb-6">
          {/* Hero Card - System Overview */}
          <div className="bg-gradient-to-r from-[#276C84] to-[#1f5668] rounded-2xl p-5 text-white shadow-xl">
            <div className="flex items-center gap-2 mb-3">
              <TrendingUp className="w-5 h-5" />
              <p className="text-white/90 text-sm">System Overview</p>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {stats.map((stat, index) => {
                const Icon = stat.icon;
                return (
                  <div key={index} className="bg-white/10 backdrop-blur-sm rounded-xl p-3">
                    <Icon className="w-5 h-5 text-white/80 mb-2" />
                    <p className="text-2xl font-bold mb-1">{stat.value}</p>
                    <p className="text-xs text-white/80">{stat.label}</p>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Quick Actions Grid */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Quick Actions</h3>
            <div className="grid grid-cols-2 gap-3">
              {quickActions.map((action, index) => {
                const Icon = action.icon;
                return (
                  <button
                    key={index}
                    onClick={() => navigate(action.route)}
                    className="bg-white p-4 rounded-2xl shadow-sm hover:shadow-lg transition-all text-left border-2 border-transparent hover:border-[#276C84] relative"
                  >
                    <div className={`${action.color} w-12 h-12 rounded-xl flex items-center justify-center mb-3`}>
                      <Icon className="w-6 h-6" />
                    </div>
                    <p className="text-sm font-semibold text-[#1E293B] mb-1">{action.label}</p>
                    <p className="text-xs text-gray-500">{action.description}</p>
                    {action.count !== undefined && (
                      <span className="absolute top-3 right-3 bg-red-500 text-white text-xs font-bold rounded-full w-6 h-6 flex items-center justify-center">
                        {action.count}
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Recent Activity */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Recent Activity</h3>
              <button className="text-sm text-[#276C84] font-semibold hover:underline">
                View All
              </button>
            </div>
            <div className="bg-white rounded-2xl p-4 shadow-sm space-y-3">
              {recentActivities.map((activity) => (
                <div key={activity.id} className="flex items-start gap-3 pb-3 border-b border-gray-100 last:border-0 last:pb-0">
                  <div className={`w-2 h-2 ${activity.color} rounded-full mt-2 flex-shrink-0`}></div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-[#1E293B]">{activity.title}</p>
                    <p className="text-xs text-gray-500 mt-1">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        
        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}