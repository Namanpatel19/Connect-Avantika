import { useNavigate } from 'react-router';
import { Users, Calendar, UserPlus, Clock, Megaphone, Settings, User, Bell, TrendingUp, FileText } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { BottomNav } from '../../components/BottomNav';

export function ClubLeadDashboard() {
  const navigate = useNavigate();

  const stats = [
    { label: 'Total Members', value: '148', icon: Users },
    { label: 'Events Held', value: '24', icon: Calendar },
    { label: 'Pending Requests', value: '3', icon: UserPlus },
    { label: 'Upcoming Events', value: '2', icon: Clock },
  ];

  const quickActions = [
    { icon: Calendar, label: 'Create Event', route: '/club-lead/create-event', color: 'bg-purple-100 text-purple-600', description: 'Plan new event' },
    { icon: UserPlus, label: 'Member Requests', route: '/club-lead/member-requests', color: 'bg-blue-100 text-blue-600', description: '3 pending', count: 3 },
    { icon: Users, label: 'Manage Members', route: '/club-lead/member-requests', color: 'bg-green-100 text-green-600', description: '148 members' },
    { icon: Clock, label: 'Schedule Interview', route: '/club-lead/interview-scheduling', color: 'bg-orange-100 text-orange-600', description: 'Set dates' },
    { icon: Megaphone, label: 'Announcements', route: '/club-lead/announcements', color: 'bg-pink-100 text-pink-600', description: 'Send updates' },
    { icon: Settings, label: 'Club Settings', route: '/club-lead/settings', color: 'bg-gray-100 text-gray-600', description: 'Edit profile' },
  ];

  const upcomingEvents = [
    { id: 1, title: 'Tech Symposium 2024', date: '2026-03-25', status: 'approved', time: '2:00 PM' },
    { id: 2, title: 'Coding Workshop', date: '2026-03-28', status: 'pending', time: '4:00 PM' },
  ];

  const pendingRequests = [
    { id: 1, name: 'Sarah Johnson', date: '2026-03-22', department: 'Computer Science' },
    { id: 2, name: 'Michael Chen', date: '2026-03-21', department: 'Engineering' },
    { id: 3, name: 'Emma Williams', date: '2026-03-20', department: 'Business' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        {/* Custom Header with Club Profile */}
        <div className="bg-gradient-to-b from-[#8B5CF6] to-[#7C3AED] px-4 pt-12 pb-24 rounded-b-[2rem]">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
                <span className="text-2xl">💻</span>
              </div>
              <div>
                <p className="text-white/80 text-sm">Managing</p>
                <h2 className="text-white text-xl font-bold">Tech Club</h2>
              </div>
            </div>
            <button 
              onClick={() => navigate('/club-lead/notifications')}
              className="bg-white/20 backdrop-blur-sm p-2.5 rounded-full relative"
            >
              <Bell className="w-5 h-5 text-white" />
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-semibold">
                4
              </span>
            </button>
          </div>
        </div>

        <div className="px-4 -mt-16 space-y-6 pb-6">
          {/* Hero Card - Club Stats */}
          <div className="bg-gradient-to-r from-[#8B5CF6] to-[#7C3AED] rounded-2xl p-5 text-white shadow-xl">
            <div className="flex items-center gap-2 mb-3">
              <TrendingUp className="w-5 h-5" />
              <p className="text-white/90 text-sm">Club Overview</p>
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
                    className="bg-white p-4 rounded-2xl shadow-sm hover:shadow-lg transition-all text-left border-2 border-transparent hover:border-[#8B5CF6] relative"
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

          {/* Upcoming Events */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Upcoming Events</h3>
              <button
                onClick={() => navigate('/club-lead/events')}
                className="text-sm text-[#8B5CF6] font-semibold hover:underline"
              >
                View All
              </button>
            </div>
            <div className="space-y-3">
              {upcomingEvents.map((event) => (
                <div 
                  key={event.id} 
                  onClick={() => navigate(`/club-lead/events/${event.id}`)}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex-1">
                      <h4 className="font-semibold text-[#1E293B] mb-2">{event.title}</h4>
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <Calendar className="w-4 h-4" />
                        {new Date(event.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} • {event.time}
                      </div>
                    </div>
                    <span className={`px-3 py-1.5 rounded-lg text-xs font-semibold ${
                      event.status === 'approved' 
                        ? 'bg-green-100 text-green-700' 
                        : 'bg-yellow-100 text-yellow-700'
                    }`}>
                      {event.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Member Requests */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Recent Requests</h3>
              <button
                onClick={() => navigate('/club-lead/member-requests')}
                className="text-sm text-[#8B5CF6] font-semibold hover:underline"
              >
                View All
              </button>
            </div>
            <div className="space-y-3">
              {pendingRequests.map((request) => (
                <div 
                  key={request.id}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-gradient-to-br from-[#8B5CF6] to-[#7C3AED] rounded-full flex items-center justify-center">
                        <User className="w-5 h-5 text-white" />
                      </div>
                      <div>
                        <p className="font-semibold text-[#1E293B] text-sm">{request.name}</p>
                        <p className="text-xs text-gray-500">{request.department}</p>
                      </div>
                    </div>
                    <button 
                      onClick={() => navigate('/club-lead/member-requests')}
                      className="bg-[#8B5CF6] text-white px-4 py-2 rounded-lg text-xs font-semibold hover:bg-[#7C3AED] transition-colors"
                    >
                      Review
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}