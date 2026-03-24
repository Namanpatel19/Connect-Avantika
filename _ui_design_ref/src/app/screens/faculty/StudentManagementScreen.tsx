import { Search, Plus, Mail, User, Award, TrendingUp } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function StudentManagementScreen() {
  const students = [
    { id: 1, name: 'Sarah Johnson', email: 'sarah.j@uni.edu', grade: 'A', attendance: '95%', department: 'Computer Science' },
    { id: 2, name: 'Michael Chen', email: 'michael.c@uni.edu', grade: 'B+', attendance: '88%', department: 'Computer Science' },
    { id: 3, name: 'Emma Williams', email: 'emma.w@uni.edu', grade: 'A-', attendance: '92%', department: 'Computer Science' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Student Management" showBack showNotifications />
        <div className="px-4 py-6 space-y-6">
          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search students..."
              className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#F59E0B]"
            />
          </div>

          {/* Add Student Button */}
          <button className="w-full bg-gradient-to-r from-[#F59E0B] to-[#D97706] text-white py-3.5 rounded-xl font-semibold hover:from-[#D97706] hover:to-[#B45309] transition-all flex items-center justify-center gap-2 shadow-sm">
            <Plus className="w-5 h-5" />
            Add Student to Course
          </button>

          {/* Student List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">My Students ({students.length})</h3>
            {students.map((student) => (
              <div key={student.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#F59E0B] to-[#D97706] rounded-full flex items-center justify-center flex-shrink-0">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-[#1E293B] mb-1">{student.name}</h4>
                    <p className="text-sm text-gray-600 mb-1">{student.department}</p>
                    <div className="flex items-center gap-2 text-xs text-gray-500">
                      <Mail className="w-3.5 h-3.5 text-gray-400" />
                      {student.email}
                    </div>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-lg p-3 grid grid-cols-2 gap-3">
                  <div className="flex items-center gap-2">
                    <div className="bg-green-100 p-1.5 rounded">
                      <Award className="w-4 h-4 text-green-600" />
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Grade</p>
                      <p className="font-semibold text-[#1E293B] text-sm">{student.grade}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="bg-blue-100 p-1.5 rounded">
                      <TrendingUp className="w-4 h-4 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">Attendance</p>
                      <p className="font-semibold text-[#1E293B] text-sm">{student.attendance}</p>
                    </div>
                  </div>
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