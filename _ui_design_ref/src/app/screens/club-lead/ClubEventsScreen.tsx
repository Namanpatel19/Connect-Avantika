import { Calendar, MapPin, Users } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { StatusBadge } from '../../components/StatusBadge';

export function ClubEventsScreen() {
  const events = [
    { id: 1, title: 'Tech Symposium 2024', date: '2024-04-15', venue: 'Main Auditorium', attendees: 200, status: 'pending' as const },
    { id: 2, title: 'Coding Workshop', date: '2024-04-10', venue: 'Lab B-201', attendees: 50, status: 'approved' as const },
    { id: 3, title: 'Hackathon 2024', date: '2024-03-28', venue: 'Tech Hub', attendees: 100, status: 'approved' as const },
    { id: 4, title: 'AI Seminar', date: '2024-03-15', venue: 'Conference Room', attendees: 75, status: 'rejected' as const },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Club Events" showBack showNotifications />

        <div className="px-4 py-6 space-y-4">
          {events.map((event) => (
            <div key={event.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
              <div className="flex justify-between items-start mb-3">
                <h4 className="font-semibold text-[#1E293B] flex-1">{event.title}</h4>
                <StatusBadge status={event.status} />
              </div>
              <div className="space-y-2">
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Calendar className="w-4 h-4" />
                  {new Date(event.date).toLocaleDateString()}
                </div>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <MapPin className="w-4 h-4" />
                  {event.venue}
                </div>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Users className="w-4 h-4" />
                  {event.attendees} expected attendees
                </div>
              </div>
            </div>
          ))}
        </div>

        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}
