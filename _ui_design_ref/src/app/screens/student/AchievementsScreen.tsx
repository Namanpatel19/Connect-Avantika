import { Trophy, Award, Star, Medal } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function AchievementsScreen() {
  const badges = [
    { id: 1, name: 'Event Participant', icon: Trophy, color: '#F59E0B' },
    { id: 2, name: 'Club Member', icon: Star, color: '#8B5CF6' },
    { id: 3, name: 'Top Performer', icon: Medal, color: '#22C55E' },
    { id: 4, name: 'Leadership', icon: Award, color: '#3B82F6' },
  ];

  const certificates = [
    { id: 1, title: 'Hackathon Winner 2024', issuer: 'Tech Club', date: '2024-03-15' },
    { id: 2, title: 'Workshop Completion', issuer: 'CS Department', date: '2024-02-28' },
    { id: 3, title: 'Volunteer Service', issuer: 'Student Office', date: '2024-01-20' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="My Achievements" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-gradient-to-r from-[#22C55E] to-[#16A34A] rounded-2xl p-6 text-white text-center">
            <Trophy className="w-16 h-16 mx-auto mb-3" />
            <h2 className="text-3xl font-bold mb-2">12</h2>
            <p className="text-white/80">Total Achievements</p>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-4">Badges</h3>
            <div className="grid grid-cols-2 gap-4">
              {badges.map((badge) => {
                const Icon = badge.icon;
                return (
                  <div key={badge.id} className="bg-white rounded-xl p-4 shadow-sm text-center">
                    <div
                      className="w-16 h-16 rounded-full mx-auto mb-3 flex items-center justify-center"
                      style={{ backgroundColor: `${badge.color}20` }}
                    >
                      <Icon className="w-8 h-8" style={{ color: badge.color }} />
                    </div>
                    <p className="font-medium text-[#1E293B] text-sm">{badge.name}</p>
                  </div>
                );
              })}
            </div>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-4">Certificates</h3>
            <div className="space-y-3">
              {certificates.map((cert) => (
                <div key={cert.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 bg-[#22C55E]/10 rounded-lg flex items-center justify-center flex-shrink-0">
                      <Award className="w-5 h-5 text-[#22C55E]" />
                    </div>
                    <div className="flex-1">
                      <h4 className="font-semibold text-[#1E293B] mb-1">{cert.title}</h4>
                      <p className="text-sm text-gray-600 mb-1">{cert.issuer}</p>
                      <p className="text-xs text-gray-400">{new Date(cert.date).toLocaleDateString()}</p>
                    </div>
                  </div>
                  <button className="w-full mt-3 bg-[#22C55E]/10 text-[#22C55E] py-2 rounded-lg text-sm font-medium hover:bg-[#22C55E]/20">
                    View Certificate
                  </button>
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
