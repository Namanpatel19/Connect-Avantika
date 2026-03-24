import { useState } from 'react';
import { Search, Plus, Edit, Trash2, Mail, Briefcase, User } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ManageFacultyScreen() {
  const [searchQuery, setSearchQuery] = useState('');
  const [filter, setFilter] = useState('all');

  const faculty = [
    { id: 1, name: 'Dr. Robert Smith', email: 'robert.s@uni.edu', department: 'Computer Science', position: 'Professor', courses: 4 },
    { id: 2, name: 'Dr. Lisa Anderson', email: 'lisa.a@uni.edu', department: 'Business', position: 'Associate Professor', courses: 3 },
    { id: 3, name: 'Dr. David Lee', email: 'david.l@uni.edu', department: 'Engineering', position: 'Professor', courses: 5 },
  ];

  const departments = ['all', 'Computer Science', 'Business', 'Engineering', 'Arts'];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Manage Faculty" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Search Bar */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search faculty..."
              className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#276C84]"
            />
          </div>

          {/* Department Filter */}
          <div className="flex gap-2 overflow-x-auto pb-2 -mx-4 px-4">
            {departments.map((dept) => (
              <button
                key={dept}
                onClick={() => setFilter(dept)}
                className={`px-4 py-2 rounded-xl text-sm font-semibold whitespace-nowrap transition-all shadow-sm ${ 
                  filter === dept
                    ? 'bg-[#276C84] text-white'
                    : 'bg-white text-gray-600 border border-gray-200 hover:border-[#276C84]'
                }`}
              >
                {dept === 'all' ? 'All Departments' : dept}
              </button>
            ))}
          </div>

          {/* Add Faculty Button */}
          <button className="w-full bg-[#276C84] text-white py-3.5 rounded-xl font-semibold hover:bg-[#1f5668] transition-colors flex items-center justify-center gap-2 shadow-sm">
            <Plus className="w-5 h-5" />
            Add New Faculty
          </button>

          {/* Faculty List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">Faculty Members ({faculty.length})</h3>
            {faculty.map((member) => (
              <div key={member.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#F59E0B] to-[#D97706] rounded-full flex items-center justify-center flex-shrink-0">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-[#1E293B] mb-1">{member.name}</h4>
                    <div className="flex items-center gap-2 text-sm text-gray-600 mb-1">
                      <Briefcase className="w-4 h-4 text-gray-400" />
                      {member.position}
                    </div>
                    <p className="text-sm text-[#276C84] font-medium">{member.department}</p>
                  </div>
                </div>
                <div className="bg-gray-50 rounded-lg p-3 mb-3 space-y-1.5">
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Mail className="w-3.5 h-3.5 text-gray-400" />
                    {member.email}
                  </div>
                  <div className="text-xs text-gray-600">
                    <span className="font-semibold">{member.courses}</span> active courses
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