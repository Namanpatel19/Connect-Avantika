import { Mail, Phone, MapPin, Calendar, Edit } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function AdminProfileScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Profile" showBack />

        <div className="px-4 py-6 space-y-6">
          {/* Profile Header */}
          <div className="bg-gradient-to-r from-[#276C84] to-[#1f5668] rounded-2xl p-6 text-center">
            <div className="w-24 h-24 bg-white/20 backdrop-blur-sm rounded-full mx-auto mb-4 flex items-center justify-center">
              <span className="text-4xl font-bold text-white">DA</span>
            </div>
            <h2 className="text-2xl font-bold text-white mb-1">Dr. Alan Thompson</h2>
            <p className="text-white/80">Dean of Students</p>
          </div>

          {/* Contact Information */}
          <div className="bg-white rounded-xl p-4 shadow-sm">
            <h3 className="font-semibold text-[#1E293B] mb-4">Contact Information</h3>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#276C84]/10 rounded-lg flex items-center justify-center">
                  <Mail className="w-5 h-5 text-[#276C84]" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Email</p>
                  <p className="text-sm font-medium text-[#1E293B]">alan.thompson@university.edu</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#276C84]/10 rounded-lg flex items-center justify-center">
                  <Phone className="w-5 h-5 text-[#276C84]" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Phone</p>
                  <p className="text-sm font-medium text-[#1E293B]">+1 (555) 123-4567</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#276C84]/10 rounded-lg flex items-center justify-center">
                  <MapPin className="w-5 h-5 text-[#276C84]" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Office</p>
                  <p className="text-sm font-medium text-[#1E293B]">Admin Building, Room 305</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-[#276C84]/10 rounded-lg flex items-center justify-center">
                  <Calendar className="w-5 h-5 text-[#276C84]" />
                </div>
                <div>
                  <p className="text-xs text-gray-500">Joined</p>
                  <p className="text-sm font-medium text-[#1E293B]">January 2020</p>
                </div>
              </div>
            </div>
          </div>

          {/* Edit Profile Button */}
          <button className="w-full bg-[#276C84] text-white py-3.5 rounded-xl font-semibold hover:bg-[#1f5668] transition-colors flex items-center justify-center gap-2 shadow-sm">
            <Edit className="w-5 h-5" />
            Edit Profile
          </button>
        </div>

        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}
