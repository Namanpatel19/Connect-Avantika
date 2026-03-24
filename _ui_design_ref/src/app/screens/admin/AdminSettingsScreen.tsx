import { Bell, Lock, Globe, Moon, LogOut, ChevronRight } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function AdminSettingsScreen() {
  const settingSections = [
    {
      title: 'Preferences',
      items: [
        { icon: Bell, label: 'Notifications', value: 'Enabled' },
        { icon: Moon, label: 'Dark Mode', value: 'Off' },
        { icon: Globe, label: 'Language', value: 'English' },
      ],
    },
    {
      title: 'Security',
      items: [
        { icon: Lock, label: 'Change Password', value: null },
        { icon: Lock, label: 'Two-Factor Auth', value: 'Enabled' },
      ],
    },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Settings" showBack />

        <div className="px-4 py-6 space-y-6">
          {settingSections.map((section) => (
            <div key={section.title}>
              <h3 className="text-sm font-semibold text-gray-500 uppercase mb-3 px-2">
                {section.title}
              </h3>
              <div className="bg-white rounded-xl shadow-sm divide-y divide-gray-100">
                {section.items.map((item) => {
                  const Icon = item.icon;
                  return (
                    <button
                      key={item.label}
                      className="w-full flex items-center justify-between p-4 hover:bg-gray-50 transition-colors"
                    >
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-[#276C84]/10 rounded-lg flex items-center justify-center">
                          <Icon className="w-5 h-5 text-[#276C84]" />
                        </div>
                        <span className="font-medium text-[#1E293B]">{item.label}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        {item.value && (
                          <span className="text-sm text-gray-500">{item.value}</span>
                        )}
                        <ChevronRight className="w-5 h-5 text-gray-400" />
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          ))}

          {/* Logout Button */}
          <button className="w-full bg-red-600 text-white py-3.5 rounded-xl font-semibold hover:bg-red-700 transition-colors flex items-center justify-center gap-2 shadow-sm mt-8">
            <LogOut className="w-5 h-5" />
            Logout
          </button>

          {/* App Version */}
          <p className="text-center text-sm text-gray-500">Version 1.0.0</p>
        </div>

        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}
