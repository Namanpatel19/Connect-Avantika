import { useState } from 'react';
import { Calendar, Users } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function EventsClubsOverviewScreen() {
  const [tab, setTab] = useState<'events' | 'clubs'>('events');
  
  const events = [
    { id: 1, title: 'Tech Symposium', date: '2024-04-15', club: 'Tech Club' },
    { id: 2, title: 'Cultural Night', date: '2024-04-20', club: 'Arts Society' },
  ];

  const clubs = [
    { id: 1, name: 'Tech Club', members: 148, category: 'Technology' },
    { id: 2, name: 'Arts Society', members: 92, category: 'Arts & Culture' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Events & Clubs" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="flex gap-2 bg-white p-1 rounded-xl">
            <button
              onClick={() => setTab('events')}
              className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                tab === 'events' ? 'bg-[#F59E0B] text-white' : 'text-gray-600'
              }`}
            >
              Events
            </button>
            <button
              onClick={() => setTab('clubs')}
              className={`flex-1 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                tab === 'clubs' ? 'bg-[#F59E0B] text-white' : 'text-gray-600'
              }`}
            >
              Clubs
            </button>
          </div>
          <div className="space-y-3">
            {tab === 'events' ? events.map((event) => (
              <div key={event.id} className="bg-white rounded-xl p-4 shadow-sm">
                <h4 className="font-semibold text-[#1E293B] mb-2">{event.title}</h4>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Calendar className="w-4 h-4" />
                  {new Date(event.date).toLocaleDateString()} • {event.club}
                </div>
              </div>
            )) : clubs.map((club) => (
              <div key={club.id} className="bg-white rounded-xl p-4 shadow-sm">
                <h4 className="font-semibold text-[#1E293B] mb-2">{club.name}</h4>
                <p className="text-sm text-gray-600 mb-2">{club.category}</p>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Users className="w-4 h-4" />
                  {club.members} members
                </div>
              </div>
            ))}
          </div>
        </div>
        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}
