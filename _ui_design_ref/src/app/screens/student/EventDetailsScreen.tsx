import { Calendar, MapPin, Users, Clock, CheckCircle } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function EventDetailsScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Event Details" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-white rounded-xl p-6 shadow-sm">
            <h2 className="text-2xl font-bold text-[#1E293B] mb-4">Tech Symposium 2024</h2>
            <p className="text-gray-600 mb-6">
              Join us for an exciting technology symposium featuring industry experts, 
              interactive workshops, and networking opportunities. Learn about the latest 
              trends in AI, blockchain, and web development.
            </p>
            <div className="space-y-3">
              {[
                { icon: Calendar, label: 'Date', value: 'April 15, 2024' },
                { icon: Clock, label: 'Time', value: '10:00 AM - 5:00 PM' },
                { icon: MapPin, label: 'Venue', value: 'Main Auditorium' },
                { icon: Users, label: 'Organizer', value: 'Tech Club' },
                { icon: Users, label: 'Attendees', value: '200 registered' },
              ].map((item) => {
                const Icon = item.icon;
                return (
                  <div key={item.label} className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-[#22C55E]/10 rounded-lg flex items-center justify-center">
                      <Icon className="w-5 h-5 text-[#22C55E]" />
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">{item.label}</p>
                      <p className="text-sm font-medium text-[#1E293B]">{item.value}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
          <button className="w-full bg-[#22C55E] text-white py-3.5 rounded-xl font-semibold hover:bg-[#16A34A] flex items-center justify-center gap-2 shadow-lg">
            <CheckCircle className="w-5 h-5" />
            Register for Event
          </button>
        </div>
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
