import { useState } from 'react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { Calendar, Clock, MapPin } from 'lucide-react';

const mockEvents = [
  {
    id: 1,
    title: 'Tech Club Meetup',
    date: '2026-03-25',
    time: '14:00',
    location: 'Room 301',
    type: 'Club Event',
    color: 'bg-blue-500',
  },
  {
    id: 2,
    title: 'Basketball Tournament Finals',
    date: '2026-03-26',
    time: '16:00',
    location: 'Sports Complex',
    type: 'Sports',
    color: 'bg-green-500',
  },
  {
    id: 3,
    title: 'Career Fair',
    date: '2026-03-28',
    time: '10:00',
    location: 'Main Hall',
    type: 'Career',
    color: 'bg-purple-500',
  },
  {
    id: 4,
    title: 'Cultural Night',
    date: '2026-03-30',
    time: '18:00',
    location: 'Auditorium',
    type: 'Cultural',
    color: 'bg-orange-500',
  },
];

const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

export function StudentCalendarScreen() {
  const [selectedDate, setSelectedDate] = useState(new Date());

  const getDaysInMonth = () => {
    const year = selectedDate.getFullYear();
    const month = selectedDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }
    for (let i = 1; i <= daysInMonth; i++) {
      days.push(i);
    }
    return days;
  };

  const getMonthYear = () => {
    return selectedDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
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

  const hasEvent = (day: number | null) => {
    if (!day) return false;
    const dateStr = `${selectedDate.getFullYear()}-${String(selectedDate.getMonth() + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    return mockEvents.some(event => event.date === dateStr);
  };

  const days = getDaysInMonth();

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Calendar" showBack />

        <div className="p-4">
          {/* Calendar Header */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-[#1E293B]">{getMonthYear()}</h2>
              <Calendar className="w-5 h-5 text-[#276C84]" />
            </div>

            {/* Day Headers */}
            <div className="grid grid-cols-7 gap-2 mb-2">
              {daysOfWeek.map(day => (
                <div key={day} className="text-center text-xs font-medium text-gray-500">
                  {day}
                </div>
              ))}
            </div>

            {/* Calendar Grid */}
            <div className="grid grid-cols-7 gap-2">
              {days.map((day, index) => (
                <div
                  key={index}
                  className={`
                    aspect-square flex items-center justify-center rounded-lg text-sm relative
                    ${day ? 'cursor-pointer hover:bg-gray-50' : ''}
                    ${isToday(day) ? 'bg-[#276C84] text-white font-semibold' : 'text-[#1E293B]'}
                    ${!day ? 'text-gray-300' : ''}
                  `}
                >
                  {day}
                  {hasEvent(day) && !isToday(day) && (
                    <div className="absolute bottom-1 w-1 h-1 bg-[#22C55E] rounded-full"></div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Upcoming Events */}
          <div className="mb-4">
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Upcoming Events</h3>
            <div className="space-y-3">
              {mockEvents.map(event => (
                <div key={event.id} className="bg-white rounded-xl shadow-sm p-4">
                  <div className="flex gap-3">
                    <div className={`${event.color} w-1 rounded-full`}></div>
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-2">
                        <div>
                          <h4 className="font-semibold text-[#1E293B] mb-1">{event.title}</h4>
                          <span className="text-xs text-gray-500">{event.type}</span>
                        </div>
                      </div>
                      <div className="flex flex-col gap-1 text-sm text-gray-600">
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

        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
