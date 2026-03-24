import { useNavigate } from 'react-router';
import { Calendar, Users, Trophy, Megaphone, Bus, User, Bell } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function StudentDashboard() {
  const navigate = useNavigate();

  const upcomingEvents = [
    { id: 1, title: 'Tech Symposium 2024', date: '2026-03-25', club: 'Tech Club', time: '2:00 PM' },
    { id: 2, title: 'Cultural Night', date: '2026-03-30', club: 'Arts Society', time: '6:00 PM' },
  ];

  const myClubs = [
    { id: 1, name: 'Tech Club', role: 'Member', logo: '💻' },
    { id: 2, name: 'Sports Club', role: 'Member', logo: '⚽' },
    { id: 3, name: 'Music Society', role: 'Member', logo: '🎵' },
  ];

  const announcements = [
    { id: 1, title: 'Mid-Term Exam Schedule Released', date: '2026-03-20' },
    { id: 2, title: 'New Scholarship Available', date: '2026-03-18' },
  ];

  const quickActions = [
    { icon: Calendar, label: 'Events', route: '/student/events', color: 'bg-blue-100 text-blue-600', count: upcomingEvents.length },
    { icon: Users, label: 'Clubs', route: '/student/clubs', color: 'bg-green-100 text-green-600', count: myClubs.length },
    { icon: Calendar, label: 'Calendar', route: '/student/calendar', color: 'bg-purple-100 text-purple-600' },
    { icon: Bus, label: 'Transport', route: '/student/transport', color: 'bg-orange-100 text-orange-600' },
    { icon: Trophy, label: 'Achievements', route: '/student/achievements', color: 'bg-yellow-100 text-yellow-600', count: 7 },
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
                <h2 className="text-white text-xl font-bold">Sarah Johnson</h2>
              </div>
            </div>
            <button 
              onClick={() => navigate('/student/notifications')}
              className="bg-white/20 backdrop-blur-sm p-2.5 rounded-full relative"
            >
              <Bell className="w-5 h-5 text-white" />
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-semibold">
                3
              </span>
            </button>
          </div>
        </div>

        <div className="px-4 -mt-16 space-y-6 pb-6">
          {/* Hero Card - Next Event */}
          <div className="bg-gradient-to-r from-[#22C55E] to-[#16A34A] rounded-2xl p-5 text-white shadow-xl">
            <div className="flex items-start justify-between mb-4">
              <div>
                <p className="text-white/90 text-sm mb-1">Next Event</p>
                <h3 className="text-xl font-bold mb-1">{upcomingEvents[0].title}</h3>
                <p className="text-white/90 text-sm">{upcomingEvents[0].club}</p>
              </div>
              <div className="bg-white/20 backdrop-blur-sm px-3 py-1.5 rounded-lg">
                <p className="text-xs text-white/90">Today</p>
                <p className="font-bold">{upcomingEvents[0].time}</p>
              </div>
            </div>
            <button 
              onClick={() => navigate('/student/events/1')}
              className="w-full bg-white text-[#22C55E] py-2.5 rounded-xl font-semibold hover:bg-white/90 transition-all"
            >
              View Details
            </button>
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
                    className="bg-white p-4 rounded-2xl shadow-sm hover:shadow-lg transition-all text-left border-2 border-transparent hover:border-[#276C84]"
                  >
                    <div className={`${action.color} w-12 h-12 rounded-xl flex items-center justify-center mb-3`}>
                      <Icon className="w-6 h-6" />
                    </div>
                    <p className="text-sm font-semibold text-[#1E293B] mb-1">{action.label}</p>
                    {action.count !== undefined && (
                      <p className="text-xs text-gray-500">{action.count} {action.label.toLowerCase()}</p>
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
                onClick={() => navigate('/student/events')}
                className="text-sm text-[#276C84] font-semibold hover:underline"
              >
                View All
              </button>
            </div>
            <div className="space-y-3">
              {upcomingEvents.map((event) => (
                <div 
                  key={event.id} 
                  onClick={() => navigate(`/student/events/${event.id}`)}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h4 className="font-semibold text-[#1E293B] mb-2">{event.title}</h4>
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <Calendar className="w-4 h-4" />
                        {new Date(event.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} • {event.time}
                      </div>
                      <p className="text-xs text-gray-500 mt-1">{event.club}</p>
                    </div>
                    <div className="bg-[#22C55E]/10 p-2 rounded-lg">
                      <Calendar className="w-5 h-5 text-[#22C55E]" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* My Clubs */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Joined Clubs</h3>
              <button
                onClick={() => navigate('/student/clubs')}
                className="text-sm text-[#276C84] font-semibold hover:underline"
              >
                View All
              </button>
            </div>
            <div className="grid grid-cols-3 gap-3">
              {myClubs.map((club) => (
                <div 
                  key={club.id} 
                  onClick={() => navigate(`/student/clubs/${club.id}`)}
                  className="bg-white rounded-2xl p-4 shadow-sm text-center hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="text-3xl mb-2">{club.logo}</div>
                  <p className="font-semibold text-[#1E293B] text-xs mb-1 line-clamp-1">{club.name}</p>
                  <p className="text-xs text-gray-500">{club.role}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Announcements */}
          <div>
            <div className="flex items-center gap-2 mb-3">
              <Megaphone className="w-5 h-5 text-[#276C84]" />
              <h3 className="text-lg font-semibold text-[#1E293B]">Announcements</h3>
            </div>
            <div className="bg-white rounded-2xl p-4 shadow-sm space-y-3">
              {announcements.map((ann) => (
                <div key={ann.id} className="pb-3 border-b border-gray-100 last:border-0 last:pb-0">
                  <p className="font-semibold text-[#1E293B] text-sm mb-1">{ann.title}</p>
                  <p className="text-xs text-gray-500">
                    {new Date(ann.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
        
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}