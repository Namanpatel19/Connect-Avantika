import { Bell, Lock, Globe, LogOut, ChevronRight } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function FacultySettingsScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Settings" showBack />
        <div className="px-4 py-6 space-y-6">
          <div className="bg-white rounded-xl shadow-sm divide-y">
            {[
              { icon: Bell, label: 'Notifications', value: 'Enabled' },
              { icon: Globe, label: 'Language', value: 'English' },
              { icon: Lock, label: 'Change Password', value: null },
            ].map((item) => {
              const Icon = item.icon;
              return (
                <button key={item.label} className="w-full flex items-center justify-between p-4 hover:bg-gray-50">
                  <div className="flex items-center gap-3">
                    <Icon className="w-5 h-5 text-[#F59E0B]" />
                    <span className="font-medium text-[#1E293B]">{item.label}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    {item.value && <span className="text-sm text-gray-500">{item.value}</span>}
                    <ChevronRight className="w-5 h-5 text-gray-400" />
                  </div>
                </button>
              );
            })}
          </div>
          <button className="w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 flex items-center justify-center gap-2">
            <LogOut className="w-5 h-5" />
            Logout
          </button>
        </div>
        <BottomNav role="faculty" />
      </div>
    </MobileContainer>
  );
}
