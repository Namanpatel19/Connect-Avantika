import { useNavigate } from 'react-router';
import { Upload, Megaphone, BookOpen, Users, Calendar, Settings, User, Bell, TrendingUp, FileText, ClipboardList } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { BottomNav } from '../../components/BottomNav';

export function FacultyDashboard() {
  const navigate = useNavigate();

  const stats = [
    { label: 'My Students', value: '120', icon: Users },
    { label: 'Active Courses', value: '4', icon: BookOpen },
    { label: 'Materials', value: '28', icon: FileText },
    { label: 'Classes/Week', value: '18', icon: Calendar },
  ];

  const quickActions = [
    { icon: Upload, label: 'Upload Material', route: '/faculty/upload-material', color: 'bg-blue-100 text-blue-600', description: 'Add resources' },
    { icon: Users, label: 'Manage Students', route: '/faculty/student-management', color: 'bg-green-100 text-green-600', description: '120 students' },
    { icon: Megaphone, label: 'Announcements', route: '/faculty/announcements', color: 'bg-purple-100 text-purple-600', description: 'Send updates' },
    { icon: ClipboardList, label: 'Attendance', route: '/faculty/attendance', color: 'bg-orange-100 text-orange-600', description: 'Mark present' },
    { icon: Calendar, label: 'View Events', route: '/faculty/events-clubs', color: 'bg-pink-100 text-pink-600', description: 'Campus events' },
    { icon: Settings, label: 'Settings', route: '/faculty/settings', color: 'bg-gray-100 text-gray-600', description: 'Preferences' },
  ];

  const recentMaterials = [
    { id: 1, subject: 'Data Structures', title: 'Lecture 15: Binary Trees', date: '2026-03-20', type: 'PDF' },
    { id: 2, subject: 'Algorithms', title: 'Assignment 4: Sorting', date: '2026-03-18', type: 'DOC' },
    { id: 3, subject: 'Database Systems', title: 'Lab 8: SQL Joins', date: '2026-03-15', type: 'PDF' },
  ];

  const upcomingClasses = [
    { id: 1, course: 'Data Structures', time: '10:00 AM', room: 'CS-301', students: 45 },
    { id: 2, course: 'Algorithms', time: '2:00 PM', room: 'CS-205', students: 38 },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        {/* Custom Header with Profile */}
        <div className="bg-gradient-to-b from-[#F59E0B] to-[#D97706] px-4 pt-12 pb-24 rounded-b-[2rem]">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center">
                <User className="w-6 h-6 text-white" />
              </div>
              <div>
                <p className="text-white/80 text-sm">Welcome back,</p>
                <h2 className="text-white text-xl font-bold">Dr. Smith</h2>
              </div>
            </div>
            <button 
              onClick={() => navigate('/faculty/notifications')}
              className="bg-white/20 backdrop-blur-sm p-2.5 rounded-full relative"
            >
              <Bell className="w-5 h-5 text-white" />
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-semibold">
                2
              </span>
            </button>
          </div>
        </div>

        <div className="px-4 -mt-16 space-y-6 pb-6">
          {/* Hero Card - Teaching Stats */}
          <div className="bg-gradient-to-r from-[#F59E0B] to-[#D97706] rounded-2xl p-5 text-white shadow-xl">
            <div className="flex items-center gap-2 mb-3">
              <TrendingUp className="w-5 h-5" />
              <p className="text-white/90 text-sm">Teaching Overview</p>
            </div>
            <div className="grid grid-cols-2 gap-3">
              {stats.map((stat, index) => {
                const Icon = stat.icon;
                return (
                  <div key={index} className="bg-white/10 backdrop-blur-sm rounded-xl p-3">
                    <Icon className="w-5 h-5 text-white/80 mb-2" />
                    <p className="text-2xl font-bold mb-1">{stat.value}</p>
                    <p className="text-xs text-white/80">{stat.label}</p>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Quick Actions Grid */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Quick Actions</h3>
            <div className="grid grid-cols-2 gap-3">
              {quickActions.map((action, index) => {
                const Icon = action.icon;
                return (
                  <button
                    key={index}
                    onClick={() => navigate(action.route)}
                    className="bg-white p-4 rounded-2xl shadow-sm hover:shadow-lg transition-all text-left border-2 border-transparent hover:border-[#F59E0B]"
                  >
                    <div className={`${action.color} w-12 h-12 rounded-xl flex items-center justify-center mb-3`}>
                      <Icon className="w-6 h-6" />
                    </div>
                    <p className="text-sm font-semibold text-[#1E293B] mb-1">{action.label}</p>
                    <p className="text-xs text-gray-500">{action.description}</p>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Today's Classes */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Today's Classes</h3>
              <button className="text-sm text-[#F59E0B] font-semibold hover:underline">
                View Schedule
              </button>
            </div>
            <div className="space-y-3">
              {upcomingClasses.map((classItem) => (
                <div 
                  key={classItem.id} 
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h4 className="font-semibold text-[#1E293B] mb-2">{classItem.course}</h4>
                      <div className="flex items-center gap-4 text-sm text-gray-600">
                        <div className="flex items-center gap-1">
                          <Calendar className="w-4 h-4" />
                          {classItem.time}
                        </div>
                        <div className="flex items-center gap-1">
                          <Users className="w-4 h-4" />
                          {classItem.students} students
                        </div>
                      </div>
                      <p className="text-xs text-gray-500 mt-1">Room: {classItem.room}</p>
                    </div>
                    <div className="bg-[#F59E0B]/10 p-2 rounded-lg">
                      <BookOpen className="w-5 h-5 text-[#F59E0B]" />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Recent Materials */}
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="text-lg font-semibold text-[#1E293B]">Recent Materials</h3>
              <button
                onClick={() => navigate('/faculty/upload-material')}
                className="text-sm text-[#F59E0B] font-semibold hover:underline"
              >
                View All
              </button>
            </div>
            <div className="space-y-3">
              {recentMaterials.map((material) => (
                <div 
                  key={material.id}
                  className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all cursor-pointer"
                >
                  <div className="flex items-start gap-3">
                    <div className="bg-[#F59E0B]/10 p-2.5 rounded-lg flex-shrink-0">
                      <FileText className="w-5 h-5 text-[#F59E0B]" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-[#1E293B] text-sm mb-1">{material.title}</p>
                      <p className="text-xs text-gray-600 mb-1">{material.subject}</p>
                      <div className="flex items-center gap-2">
                        <span className="text-xs text-gray-500">
                          {new Date(material.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                        </span>
                        <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded text-xs font-medium">
                          {material.type}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}