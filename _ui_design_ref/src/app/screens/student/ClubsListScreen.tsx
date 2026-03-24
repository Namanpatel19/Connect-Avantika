import { Users, Search, TrendingUp, Star, Sparkles } from 'lucide-react';
import { useNavigate } from 'react-router';
import { useState } from 'react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

const popularityBadges = {
  trending: { label: 'Trending', icon: '🔥', color: 'bg-orange-100 text-orange-700 border-orange-300' },
  topClub: { label: 'Top Club', icon: '⭐', color: 'bg-yellow-100 text-yellow-700 border-yellow-300' },
  new: { label: 'New', icon: '✨', color: 'bg-blue-100 text-blue-700 border-blue-300' },
};

export function ClubsListScreen() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('all');
  
  const clubs = [
    { 
      id: 1, 
      name: 'Tech Club', 
      category: 'Technology', 
      members: 248, 
      description: 'Innovation & Technology',
      badge: 'topClub' as const,
      logo: '💻',
      joined: true,
    },
    { 
      id: 2, 
      name: 'Arts Society', 
      category: 'Arts & Culture', 
      members: 192, 
      description: 'Creative Arts & Performance',
      badge: 'trending' as const,
      logo: '🎨',
      joined: false,
    },
    { 
      id: 3, 
      name: 'Sports Club', 
      category: 'Sports', 
      members: 310, 
      description: 'Athletics & Fitness',
      badge: 'topClub' as const,
      logo: '⚽',
      joined: true,
    },
    { 
      id: 4, 
      name: 'Business Club', 
      category: 'Business', 
      members: 156, 
      description: 'Entrepreneurship & Finance',
      badge: 'new' as const,
      logo: '💼',
      joined: false,
    },
    { 
      id: 5, 
      name: 'Music Society', 
      category: 'Arts & Culture', 
      members: 124, 
      description: 'Music & Performance',
      badge: 'trending' as const,
      logo: '🎵',
      joined: true,
    },
  ];

  const filteredClubs = clubs.filter(club => {
    if (activeTab === 'all') return true;
    if (activeTab === 'my-clubs') return club.joined;
    if (activeTab === 'popular') return club.badge === 'topClub' || club.badge === 'trending';
    return true;
  });

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Clubs" showBack showNotifications />
        
        <div className="px-4 py-6 space-y-6">
          {/* Filter Tabs */}
          <div className="flex gap-2 overflow-x-auto pb-2">
            {[
              { id: 'all', label: 'All' },
              { id: 'my-clubs', label: 'My Clubs' },
              { id: 'popular', label: 'Popular' },
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-4 py-2 rounded-xl font-medium text-sm whitespace-nowrap transition-all ${
                  activeTab === tab.id
                    ? 'bg-[#276C84] text-white shadow-md'
                    : 'bg-white text-gray-600 hover:bg-gray-50'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search clubs..."
              className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#22C55E]"
            />
          </div>

          {/* Clubs Grid */}
          <div className="grid grid-cols-1 gap-4">
            {filteredClubs.map((club) => {
              const badgeData = popularityBadges[club.badge];
              
              return (
                <div
                  key={club.id}
                  onClick={() => navigate(`/student/clubs/${club.id}`)}
                  className="bg-white rounded-2xl overflow-hidden shadow-md border-2 border-transparent hover:border-[#276C84] hover:shadow-xl transition-all cursor-pointer"
                >
                  {/* Club Header */}
                  <div className="p-4 bg-gradient-to-r from-gray-50 to-white border-b">
                    <div className="flex items-start gap-3">
                      <div className="w-14 h-14 bg-gradient-to-br from-[#276C84] to-[#22C55E] rounded-2xl flex items-center justify-center text-2xl flex-shrink-0 shadow-lg">
                        {club.logo}
                      </div>
                      <div className="flex-1">
                        <h4 className="font-bold text-[#1E293B] mb-1">{club.name}</h4>
                        <p className="text-sm text-gray-600 mb-2">{club.description}</p>
                        <div className="flex items-center gap-2 flex-wrap">
                          <span className={`px-2.5 py-1 rounded-full text-xs font-semibold border-2 ${badgeData.color}`}>
                            {badgeData.icon} {badgeData.label}
                          </span>
                          <span className="bg-gray-100 text-gray-700 px-2.5 py-1 rounded-full text-xs font-medium">
                            {club.category}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Club Details */}
                  <div className="p-4">
                    <div className="flex items-center justify-between mb-4">
                      <div className="flex items-center gap-2 text-gray-600">
                        <Users className="w-5 h-5" />
                        <span className="font-semibold text-[#1E293B]">{club.members}</span>
                        <span className="text-sm">members</span>
                      </div>
                      {club.joined && (
                        <span className="bg-[#22C55E]/10 text-[#22C55E] px-3 py-1 rounded-full text-xs font-semibold border border-[#22C55E]/20">
                          Joined ✓
                        </span>
                      )}
                    </div>
                    
                    <button 
                      className={`w-full py-3 rounded-xl text-sm font-semibold transition-all shadow-md ${
                        club.joined
                          ? 'bg-[#276C84] text-white hover:bg-[#1f5668]'
                          : 'bg-[#22C55E] text-white hover:bg-[#16A34A]'
                      }`}
                    >
                      {club.joined ? 'View Club' : 'Join Club'}
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