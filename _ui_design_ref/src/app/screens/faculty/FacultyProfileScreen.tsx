import { Mail, Phone, Briefcase, Calendar, Edit } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function FacultyProfileScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Profile" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-gradient-to-r from-[#F59E0B] to-[#D97706] rounded-2xl p-6 text-center">
            <div className="w-24 h-24 bg-white/20 rounded-full mx-auto mb-4 flex items-center justify-center">
              <span className="text-4xl font-bold text-white">RP</span>
            </div>
            <h2 className="text-2xl font-bold text-white mb-1">Dr. Robert Parker</h2>
            <p className="text-white/80">Professor of Computer Science</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm space-y-3">
            {[
              { icon: Mail, label: 'Email', value: 'robert.parker@university.edu' },
              { icon: Phone, label: 'Phone', value: '+1 (555) 234-5678' },
              { icon: Briefcase, label: 'Department', value: 'Computer Science' },
              { icon: Calendar, label: 'Joined', value: 'August 2018' },
            ].map((item) => {
              const Icon = item.icon;
              return (
                <div key={item.label} className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-[#F59E0B]/10 rounded-lg flex items-center justify-center">
                    <Icon className="w-5 h-5 text-[#F59E0B]" />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500">{item.label}</p>
                    <p className="text-sm font-medium text-[#1E293B]">{item.value}</p>
                  </div>
                </div>
              );
            })}
          </div>
          <button className="w-full bg-[#F59E0B] text-white py-3.5 rounded-xl font-semibold hover:bg-[#D97706] flex items-center justify-center gap-2">
            <Edit className="w-5 h-5" />
            Edit Profile
          </button>
        </div>
        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}
