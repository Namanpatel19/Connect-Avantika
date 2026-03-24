import { useState } from 'react';
import { Search, Plus, Edit, Trash2, Mail, Phone, User } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ManageStudentsScreen() {
  const [searchQuery, setSearchQuery] = useState('');

  const students = [
    { id: 1, name: 'Sarah Johnson', email: 'sarah.j@uni.edu', department: 'Computer Science', year: '3rd Year', phone: '+1 234-567-8901', status: 'Active' },
    { id: 2, name: 'Michael Chen', email: 'michael.c@uni.edu', department: 'Business', year: '2nd Year', phone: '+1 234-567-8902', status: 'Active' },
    { id: 3, name: 'Emma Williams', email: 'emma.w@uni.edu', department: 'Engineering', year: '4th Year', phone: '+1 234-567-8903', status: 'Active' },
    { id: 4, name: 'James Rodriguez', email: 'james.r@uni.edu', department: 'Arts', year: '1st Year', phone: '+1 234-567-8904', status: 'Active' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Manage Students" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search students..."
              className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#276C84] focus:border-transparent"
            />
          </div>

          {/* Add Student Button */}
          <button className="w-full bg-[#276C84] text-white py-3.5 rounded-xl font-semibold hover:bg-[#1f5668] transition-colors flex items-center justify-center gap-2 shadow-sm">
            <Plus className="w-5 h-5" />
            Add New Student
          </button>

          {/* Student List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">All Students ({students.length})</h3>
            {students.map((student) => (
              <div key={student.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#276C84] to-[#1f5668] rounded-full flex items-center justify-center flex-shrink-0">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <h4 className="font-semibold text-[#1E293B]">{student.name}</h4>
                      <span className="bg-green-100 text-green-700 px-2 py-1 rounded-lg text-xs font-semibold">
                        {student.status}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 mb-2">{student.department}</p>
                    <p className="text-xs text-[#276C84] font-medium">{student.year}</p>
                  </div>
                </div>
                <div className="space-y-1.5 mb-3 bg-gray-50 rounded-lg p-3">
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Mail className="w-3.5 h-3.5 text-gray-400" />
                    {student.email}
                  </div>
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Phone className="w-3.5 h-3.5 text-gray-400" />
                    {student.phone}
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