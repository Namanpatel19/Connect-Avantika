import { Users, Calendar, Trophy, UserPlus } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ClubDetailsScreen() {
  const members = [
    { id: 1, name: 'Sarah Johnson', role: 'President' },
    { id: 2, name: 'Michael Chen', role: 'Vice President' },
    { id: 3, name: 'Emma Williams', role: 'Member' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Club Details" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-gradient-to-r from-[#22C55E] to-[#16A34A] rounded-2xl p-6 text-white text-center">
            <div className="w-20 h-20 bg-white/20 rounded-full mx-auto mb-4 flex items-center justify-center">
              <Users className="w-10 h-10" />
            </div>
            <h2 className="text-2xl font-bold mb-2">Tech Club</h2>
            <p className="text-white/80">Innovation & Technology</p>
          </div>

          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-[#1E293B] mb-3">About</h3>
            <p className="text-gray-600 text-sm">
              Leading technology innovation on campus through workshops, hackathons, 
              and collaborative projects. We focus on emerging technologies and provide 
              hands-on learning experiences.
            </p>
          </div>

          <div className="grid grid-cols-3 gap-4">
            {[
              { icon: Users, label: 'Members', value: '148' },
              { icon: Calendar, label: 'Events', value: '24' },
              { icon: Trophy, label: 'Awards', value: '12' },
            ].map((stat) => {
              const Icon = stat.icon;
              return (
                <div key={stat.label} className="bg-white rounded-xl p-4 shadow-sm text-center">
                  <Icon className="w-6 h-6 text-[#22C55E] mx-auto mb-2" />
                  <p className="text-2xl font-bold text-[#1E293B]">{stat.value}</p>
                  <p className="text-xs text-gray-500">{stat.label}</p>
                </div>
              );
            })}
          </div>

          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-[#1E293B] mb-3">Leadership</h3>
            <div className="space-y-3">
              {members.map((member) => (
                <div key={member.id} className="flex items-center justify-between pb-3 border-b border-gray-100 last:border-0 last:pb-0">
                  <div>
                    <p className="font-medium text-[#1E293B]">{member.name}</p>
                    <p className="text-xs text-gray-500">{member.role}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <button className="w-full bg-[#22C55E] text-white py-3.5 rounded-xl font-semibold hover:bg-[#16A34A] flex items-center justify-center gap-2 shadow-lg">
            <UserPlus className="w-5 h-5" />
            Request to Join
          </button>
        </div>
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
