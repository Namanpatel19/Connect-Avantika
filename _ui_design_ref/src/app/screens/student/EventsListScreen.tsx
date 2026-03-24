import { Calendar, MapPin, Users, Search, Lock, CheckCircle2, Trophy, Star } from 'lucide-react';
import { useNavigate } from 'react-router';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

const difficultyConfig = {
  beginner: { color: 'bg-green-100 text-green-700 border-green-300', icon: '🎯' },
  intermediate: { color: 'bg-yellow-100 text-yellow-700 border-yellow-300', icon: '⚡' },
  advanced: { color: 'bg-red-100 text-red-700 border-red-300', icon: '🔥' },
};

const statusConfig = {
  available: { icon: Star, color: 'text-[#22C55E]', bg: 'bg-[#22C55E]/10' },
  locked: { icon: Lock, color: 'text-gray-400', bg: 'bg-gray-100' },
  completed: { icon: CheckCircle2, color: 'text-[#276C84]', bg: 'bg-[#276C84]/10' },
};

export function EventsListScreen() {
  const navigate = useNavigate();
  
  const events = [
    { 
      id: 1, 
      title: 'Tech Symposium 2024', 
      date: '2024-04-15', 
      venue: 'Main Auditorium', 
      club: 'Tech Club', 
      attendees: 200,
      difficulty: 'beginner' as const,
      status: 'available' as const,
      points: 100,
      progress: 0,
    },
    { 
      id: 2, 
      title: 'Cultural Night', 
      date: '2024-04-20', 
      venue: 'Campus Grounds', 
      club: 'Arts Society', 
      attendees: 500,
      difficulty: 'intermediate' as const,
      status: 'available' as const,
      points: 250,
      progress: 0,
    },
    { 
      id: 3, 
      title: 'Coding Workshop', 
      date: '2024-04-10', 
      venue: 'Lab B-201', 
      club: 'CS Club', 
      attendees: 50,
      difficulty: 'advanced' as const,
      status: 'completed' as const,
      points: 500,
      progress: 100,
    },
    { 
      id: 4, 
      title: 'Basketball Tournament Finals', 
      date: '2024-04-28', 
      venue: 'Sports Complex', 
      club: 'Sports Club', 
      attendees: 300,
      difficulty: 'intermediate' as const,
      status: 'locked' as const,
      points: 200,
      progress: 0,
    },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Events" showBack showNotifications />
        
        <div className="px-4 py-6 space-y-6">
          {/* Points Banner */}
          <div className="bg-gradient-to-r from-[#F59E0B] to-[#F97316] rounded-2xl p-4 shadow-lg">
            <div className="flex items-center justify-between text-white">
              <div className="flex items-center gap-3">
                <div className="bg-white/20 p-2.5 rounded-xl">
                  <Trophy className="w-6 h-6" />
                </div>
                <div>
                  <p className="text-sm text-white/90">Total Points</p>
                  <p className="text-2xl font-bold">1,250</p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-sm text-white/90">Events Completed</p>
                <p className="text-2xl font-bold">7</p>
              </div>
            </div>
          </div>

          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search events..."
              className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#22C55E]"
            />
          </div>

          {/* Events List */}
          <div className="space-y-4">
            {events.map((event) => {
              const statusData = statusConfig[event.status];
              const StatusIcon = statusData.icon;
              const difficultyData = difficultyConfig[event.difficulty];
              const isLocked = event.status === 'locked';
              const isCompleted = event.status === 'completed';
              
              return (
                <div
                  key={event.id}
                  onClick={() => !isLocked && navigate(`/student/events/${event.id}`)}
                  className={`bg-white rounded-2xl overflow-hidden shadow-md border-2 transition-all ${
                    isLocked ? 'opacity-60 cursor-not-allowed border-gray-200' : 'cursor-pointer hover:shadow-xl border-transparent'
                  }`}
                >
                  {/* Event Header with Badges */}
                  <div className="p-4 bg-gradient-to-r from-gray-50 to-white border-b">
                    <div className="flex items-start justify-between mb-2">
                      <h4 className="font-bold text-[#1E293B] flex-1 pr-2">{event.title}</h4>
                      <div className={`${statusData.bg} p-2 rounded-lg`}>
                        <StatusIcon className={`w-5 h-5 ${statusData.color}`} />
                      </div>
                    </div>
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className={`px-3 py-1 rounded-full text-xs font-semibold border-2 ${difficultyData.color}`}>
                        {difficultyData.icon} {event.difficulty}
                      </span>
                      <span className="px-3 py-1 rounded-full text-xs font-semibold bg-[#F59E0B]/10 text-[#F59E0B] border-2 border-[#F59E0B]/20">
                        <Trophy className="w-3 h-3 inline mr-1" />
                        {event.points} pts
                      </span>
                    </div>
                  </div>

                  {/* Event Details */}
                  <div className="p-4">
                    <div className="space-y-2 mb-4">
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <Calendar className="w-4 h-4" />
                        {new Date(event.date).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <MapPin className="w-4 h-4" />
                        {event.venue}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <Users className="w-4 h-4" />
                        {event.attendees} attending • {event.club}
                      </div>
                    </div>

                    {/* Progress Bar */}
                    {event.progress > 0 && (
                      <div className="mb-4">
                        <div className="flex items-center justify-between text-xs text-gray-600 mb-2">
                          <span>Progress</span>
                          <span className="font-semibold">{event.progress}%</span>
                        </div>
                        <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
                          <div 
                            className="h-full bg-gradient-to-r from-[#22C55E] to-[#16A34A] rounded-full transition-all"
                            style={{ width: `${event.progress}%` }}
                          ></div>
                        </div>
                      </div>
                    )}

                    {/* Action Button */}
                    <button 
                      className={`w-full py-3 rounded-xl text-sm font-semibold transition-all ${
                        isLocked 
                          ? 'bg-gray-200 text-gray-500 cursor-not-allowed' 
                          : isCompleted
                          ? 'bg-[#276C84] text-white hover:bg-[#1f5668]'
                          : 'bg-[#22C55E] text-white hover:bg-[#16A34A] shadow-md'
                      }`}
                      disabled={isLocked}
                    >
                      {isLocked ? (
                        <>
                          <Lock className="w-4 h-4 inline mr-2" />
                          Locked
                        </>
                      ) : isCompleted ? (
                        <>
                          <CheckCircle2 className="w-4 h-4 inline mr-2" />
                          Completed
                        </>
                      ) : (
                        'Register Now'
                      )}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
        
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}