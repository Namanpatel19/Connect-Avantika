import { useState } from 'react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { Calendar, Clock, MapPin } from 'lucide-react';

const mockEvents = [
  {
    id: 1,
    title: 'Tech Symposium 2024',
    date: '2026-03-25',
    time: '14:00',
    location: 'Main Auditorium',
    type: 'Club Event',
    color: 'bg-purple-500',
    status: 'Approved',
  },
  {
    id: 2,
    title: 'Coding Workshop',
    date: '2026-03-27',
    time: '10:00',
    location: 'Lab B-201',
    type: 'Workshop',
    color: 'bg-blue-500',
    status: 'Pending',
  },
  {
    id: 3,
    title: 'Member Meeting',
    date: '2026-03-28',
    time: '16:00',
    location: 'Club Room',
    type: 'Meeting',
    color: 'bg-green-500',
    status: 'Approved',
  },
];

const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export function ClubLeadCalendarScreen() {
  const [selectedDate, setSelectedDate] = useState(new Date());

  const getDaysInMonth = () => {
    const year = selectedDate.getFullYear();
    const month = selectedDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];
    // Add empty cells for days before the first day of the month
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    // Add the days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(day);
    }
    return days;
  };

  const getMonthYear = () => {
    return selectedDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  };

  const hasEvent = (day: number | null) => {
    if (!day) return false;
    const dateStr = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), day)
      .toISOString()
      .split('T')[0];
    return mockEvents.some(event => event.date === dateStr);
  };

  const isToday = (day: number | null) => {
    if (!day) return false;
    const today = new Date();
    return (
      day === today.getDate() &&
      selectedDate.getMonth() === today.getMonth() &&
      selectedDate.getFullYear() === today.getFullYear()
    );
  };

  const upcomingEvents = mockEvents
    .filter(event => new Date(event.date) >= new Date())
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
    .slice(0, 5);

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Event Calendar" showBack />

        <div className="p-4">
          {/* Calendar Header */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-[#1E293B]">{getMonthYear()}</h2>
              <Calendar className="w-5 h-5 text-[#8B5CF6]" />
            </div>

            {/* Days of Week */}
            <div className="grid grid-cols-7 gap-2 mb-2">
              {daysOfWeek.map(day => (
                <div key={day} className="text-center text-xs font-medium text-gray-500 py-2">
                  {day}
                </div>
              ))}
            </div>

            {/* Calendar Grid */}
            <div className="grid grid-cols-7 gap-2">
              {getDaysInMonth().map((day, index) => (
                <button
                  key={index}
                  className={`
                    aspect-square rounded-lg flex items-center justify-center text-sm relative
                    ${day ? 'hover:bg-gray-50' : ''}
                    ${isToday(day) ? 'bg-[#8B5CF6] text-white font-bold' : ''}
                    ${!isToday(day) && day ? 'text-gray-700' : ''}
                  `}
                  disabled={!day}
                >
                  {day}
                  {hasEvent(day) && !isToday(day) && (
                    <span className="absolute bottom-1 w-1 h-1 bg-[#8B5CF6] rounded-full"></span>
                  )}
                </button>
              ))}
            </div>
          </div>

          {/* Upcoming Events */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Upcoming Club Events</h3>
            <div className="space-y-3">
              {upcomingEvents.map(event => (
                <div key={event.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
                  <div className="flex items-start gap-3">
                    <div className={`${event.color} w-1 h-full rounded-full`}></div>
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-2">
                        <div>
                          <h4 className="font-semibold text-[#1E293B]">{event.title}</h4>
                          <span className="inline-block text-xs px-2 py-1 rounded-full bg-purple-100 text-purple-700 mt-1">
                            {event.type}
                          </span>
                        </div>
                        <span className={`text-xs px-2 py-1 rounded-full ${
                          event.status === 'Approved' 
                            ? 'bg-green-100 text-green-700' 
                            : 'bg-yellow-100 text-yellow-700'
                        }`}>
                          {event.status}
                        </span>
                      </div>
                      <div className="space-y-1 text-sm text-gray-600">
                        <div className="flex items-center gap-2">
                          <Calendar className="w-4 h-4" />
                          <span>{new Date(event.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Clock className="w-4 h-4" />
                          <span>{event.time}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <MapPin className="w-4 h-4" />
                          <span>{event.location}</span>
                        </div>
                      </div>
                    </div>
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
