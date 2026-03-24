import { Plus, Megaphone, Calendar, Edit, Trash2 } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function FacultyAnnouncementsScreen() {
  const announcements = [
    { id: 1, title: 'Mid-Term Exam Schedule', content: 'Exam dates have been finalized for all courses', date: '2024-03-20', subject: 'Computer Science' },
    { id: 2, title: 'Assignment Deadline Extended', content: 'Due date moved to next week for Database assignment', date: '2024-03-18', subject: 'Database Systems' },
    { id: 3, title: 'Class Cancelled', content: 'Tomorrow\'s Algorithms class is cancelled', date: '2024-03-16', subject: 'Algorithms' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Announcements" showBack showNotifications />
        <div className="px-4 py-6 space-y-6">
          {/* Create Button */}
          <button className="w-full bg-gradient-to-r from-[#F59E0B] to-[#D97706] text-white py-3.5 rounded-xl font-semibold hover:from-[#D97706] hover:to-[#B45309] transition-all flex items-center justify-center gap-2 shadow-sm">
            <Plus className="w-5 h-5" />
            Create New Announcement
          </button>

          {/* Announcements List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">My Announcements</h3>
            {announcements.map((ann) => (
              <div key={ann.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-orange-100 to-yellow-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <Megaphone className="w-6 h-6 text-orange-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-[#1E293B] mb-1">{ann.title}</h4>
                    <p className="text-sm text-gray-600 mb-2">{ann.content}</p>
                    <p className="text-xs text-[#F59E0B] font-medium">{ann.subject}</p>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-lg p-3 mb-3 flex items-center gap-2 text-xs text-gray-600">
                  <Calendar className="w-3.5 h-3.5 text-gray-400" />
                  {new Date(ann.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                </div>

                <div className="flex gap-2">
                  <button className="flex-1 bg-[#F59E0B] text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-[#D97706] transition-colors flex items-center justify-center gap-1">
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
        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}