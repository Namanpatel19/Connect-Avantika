import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { User, Mail, Phone, Calendar, MapPin, Award, Edit, LogOut } from 'lucide-react';

export function StudentProfileScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="My Profile" showBack />

        <div className="p-4">
          {/* Profile Header */}
          <div className="bg-white rounded-2xl shadow-sm p-6 mb-4">
            <div className="flex flex-col items-center mb-6">
              <div className="relative">
                <div className="w-24 h-24 bg-gradient-to-br from-[#276C84] to-[#22C55E] rounded-full flex items-center justify-center mb-3">
                  <User className="w-12 h-12 text-white" />
                </div>
                <button className="absolute bottom-0 right-0 bg-[#276C84] p-2 rounded-full shadow-lg">
                  <Edit className="w-4 h-4 text-white" />
                </button>
              </div>
              <h2 className="text-xl font-bold text-[#1E293B] mb-1">Sarah Johnson</h2>
              <p className="text-gray-500">Computer Science</p>
              <div className="flex items-center gap-2 mt-2">
                <span className="bg-[#276C84]/10 text-[#276C84] px-3 py-1 rounded-full text-xs font-medium">
                  Student ID: 2024001
                </span>
              </div>
            </div>

            {/* Quick Stats */}
            <div className="grid grid-cols-3 gap-4 pt-4 border-t">
              <div className="text-center">
                <div className="text-2xl font-bold text-[#276C84] mb-1">3</div>
                <div className="text-xs text-gray-500">Clubs</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-[#276C84] mb-1">12</div>
                <div className="text-xs text-gray-500">Events</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-[#276C84] mb-1">7</div>
                <div className="text-xs text-gray-500">Achievements</div>
              </div>
            </div>
          </div>

          {/* Personal Information */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <h3 className="font-semibold text-[#1E293B] mb-4">Personal Information</h3>
            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <div className="bg-blue-100 p-2 rounded-lg">
                  <Mail className="w-5 h-5 text-blue-600" />
                </div>
                <div className="flex-1">
                  <p className="text-xs text-gray-500 mb-1">Email</p>
                  <p className="text-sm font-medium text-[#1E293B]">sarah.johnson@university.edu</p>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="bg-green-100 p-2 rounded-lg">
                  <Phone className="w-5 h-5 text-green-600" />
                </div>
                <div className="flex-1">
                  <p className="text-xs text-gray-500 mb-1">Phone</p>
                  <p className="text-sm font-medium text-[#1E293B]">+1 (555) 123-4567</p>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="bg-purple-100 p-2 rounded-lg">
                  <Calendar className="w-5 h-5 text-purple-600" />
                </div>
                <div className="flex-1">
                  <p className="text-xs text-gray-500 mb-1">Date of Birth</p>
                  <p className="text-sm font-medium text-[#1E293B]">January 15, 2003</p>
                </div>
              </div>

              <div className="flex items-center gap-3">
                <div className="bg-orange-100 p-2 rounded-lg">
                  <MapPin className="w-5 h-5 text-orange-600" />
                </div>
                <div className="flex-1">
                  <p className="text-xs text-gray-500 mb-1">Address</p>
                  <p className="text-sm font-medium text-[#1E293B]">123 University Ave, Campus City</p>
                </div>
              </div>
            </div>
          </div>

          {/* Academic Information */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <h3 className="font-semibold text-[#1E293B] mb-4">Academic Information</h3>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Department</span>
                <span className="text-sm font-medium text-[#1E293B]">Computer Science</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Year</span>
                <span className="text-sm font-medium text-[#1E293B]">3rd Year</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">Semester</span>
                <span className="text-sm font-medium text-[#1E293B]">Spring 2026</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-gray-600">GPA</span>
                <span className="text-sm font-medium text-[#276C84]">3.8 / 4.0</span>
              </div>
            </div>
          </div>

          {/* Recent Achievements */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold text-[#1E293B]">Recent Achievements</h3>
              <Award className="w-5 h-5 text-[#22C55E]" />
            </div>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center">
                  🏆
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-[#1E293B]">Event Enthusiast</p>
                  <p className="text-xs text-gray-500">Attended 10+ events</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                  ⭐
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-[#1E293B]">Club Leader</p>
                  <p className="text-xs text-gray-500">Active in 3+ clubs</p>
                </div>
              </div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="space-y-3">
            <button className="w-full bg-[#276C84] text-white py-3 rounded-xl font-medium flex items-center justify-center gap-2 hover:bg-[#1f5668] transition-colors">
              <Edit className="w-5 h-5" />
              Edit Profile
            </button>
            <button className="w-full bg-red-50 text-red-600 py-3 rounded-xl font-medium flex items-center justify-center gap-2 hover:bg-red-100 transition-colors">
              <LogOut className="w-5 h-5" />
              Logout
            </button>
          </div>
        </div>

        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
