import { Edit, Users, Calendar, Trophy } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ClubProfileScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Club Profile" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-gradient-to-r from-[#8B5CF6] to-[#7C3AED] rounded-2xl p-6 text-white text-center">
            <div className="w-20 h-20 bg-white/20 rounded-full mx-auto mb-4 flex items-center justify-center">
              <Users className="w-10 h-10" />
            </div>
            <h2 className="text-2xl font-bold mb-2">Tech Club</h2>
            <p className="text-white/80">Innovation & Technology</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-[#1E293B] mb-3">About</h3>
            <p className="text-gray-600 text-sm">Leading technology innovation on campus through workshops, events, and collaborative projects.</p>
          </div>
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-white rounded-xl p-4 shadow-sm text-center">
              <Users className="w-6 h-6 text-[#8B5CF6] mx-auto mb-2" />
              <p className="text-2xl font-bold text-[#1E293B]">148</p>
              <p className="text-xs text-gray-500">Members</p>
            </div>
            <div className="bg-white rounded-xl p-4 shadow-sm text-center">
              <Calendar className="w-6 h-6 text-[#F59E0B] mx-auto mb-2" />
              <p className="text-2xl font-bold text-[#1E293B]">24</p>
              <p className="text-xs text-gray-500">Events</p>
            </div>
            <div className="bg-white rounded-xl p-4 shadow-sm text-center">
              <Trophy className="w-6 h-6 text-[#22C55E] mx-auto mb-2" />
              <p className="text-2xl font-bold text-[#1E293B]">12</p>
              <p className="text-xs text-gray-500">Awards</p>
            </div>
          </div>
          <button className="w-full bg-[#8B5CF6] text-white py-3.5 rounded-xl font-semibold hover:bg-[#7C3AED] transition-colors flex items-center justify-center gap-2">
            <Edit className="w-5 h-5" />
            Edit Club Profile
          </button>
        </div>
        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}
