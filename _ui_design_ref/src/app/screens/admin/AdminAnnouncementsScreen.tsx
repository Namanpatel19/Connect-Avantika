import { Plus, Megaphone, Calendar, Users, Edit, Trash2 } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function AdminAnnouncementsScreen() {
  const announcements = [
    {
      id: 1,
      title: 'University Orientation 2024',
      content: 'New student orientation will be held on March 25th at the Main Auditorium.',
      date: '2024-03-20',
      audience: 'All Students',
      priority: 'high',
    },
    {
      id: 2,
      title: 'Mid-Term Exam Schedule Released',
      content: 'Mid-term examinations will begin from April 1st. Check your portal for details.',
      date: '2024-03-18',
      audience: 'All Students',
      priority: 'medium',
    },
    {
      id: 3,
      title: 'Faculty Meeting Notice',
      content: 'Quarterly faculty meeting scheduled for March 28th at 10:00 AM.',
      date: '2024-03-15',
      audience: 'Faculty',
      priority: 'low',
    },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Announcements" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Create Announcement Button */}
          <button className="w-full bg-gradient-to-r from-[#276C84] to-[#1f5668] text-white py-3.5 rounded-xl font-semibold hover:from-[#1f5668] hover:to-[#18495a] transition-all flex items-center justify-center gap-2 shadow-sm">
            <Plus className="w-5 h-5" />
            Create New Announcement
          </button>

          {/* Announcements List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">All Announcements</h3>
            {announcements.map((announcement) => (
              <div key={announcement.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className={`w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0 ${
                    announcement.priority === 'high' ? 'bg-gradient-to-br from-red-100 to-orange-100' :
                    announcement.priority === 'medium' ? 'bg-gradient-to-br from-yellow-100 to-orange-100' : 
                    'bg-gradient-to-br from-blue-100 to-cyan-100'
                  }`}>
                    <Megaphone className={`w-6 h-6 ${
                      announcement.priority === 'high' ? 'text-red-600' :
                      announcement.priority === 'medium' ? 'text-yellow-600' : 'text-blue-600'
                    }`} />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <h4 className="font-semibold text-[#1E293B]">{announcement.title}</h4>
                      <span className={`px-2 py-1 rounded-lg text-xs font-semibold ${
                        announcement.priority === 'high' ? 'bg-red-100 text-red-700' :
                        announcement.priority === 'medium' ? 'bg-yellow-100 text-yellow-700' : 
                        'bg-blue-100 text-blue-700'
                      }`}>
                        {announcement.priority}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 mb-2">{announcement.content}</p>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-lg p-3 mb-3 flex flex-wrap gap-3 text-xs text-gray-600">
                  <div className="flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5 text-gray-400" />
                    {new Date(announcement.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </div>
                  <div className="flex items-center gap-1">
                    <Users className="w-3.5 h-3.5 text-gray-400" />
                    {announcement.audience}
                  </div>
                </div>

                <div className="flex gap-2">
                  <button className="flex-1 bg-[#276C84] text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-[#1f5668] transition-colors flex items-center justify-center gap-1">
                    <Edit className="w-4 h-4" />
                    Edit
                  </button>
                  <button className="flex-1 bg-red-50 text-red-600 py-2.5 rounded-xl text-sm font-semibold hover:bg-red-100 transition-colors flex items-center justify-center gap-1">
                    <Trash2 className="w-4 h-4" />
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}