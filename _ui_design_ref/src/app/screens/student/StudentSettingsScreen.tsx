import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { 
  Bell, 
  Lock, 
  Globe, 
  Moon, 
  HelpCircle, 
  FileText, 
  Shield,
  ChevronRight,
  Smartphone
} from 'lucide-react';

const settingsSections = [
  {
    title: 'Preferences',
    items: [
      { icon: Bell, label: 'Notifications', value: 'On', color: 'bg-blue-100 text-blue-600' },
      { icon: Moon, label: 'Dark Mode', value: 'Off', color: 'bg-purple-100 text-purple-600' },
      { icon: Globe, label: 'Language', value: 'English', color: 'bg-green-100 text-green-600' },
    ],
  },
  {
    title: 'Security & Privacy',
    items: [
      { icon: Lock, label: 'Change Password', color: 'bg-red-100 text-red-600' },
      { icon: Shield, label: 'Privacy Settings', color: 'bg-orange-100 text-orange-600' },
      { icon: Smartphone, label: 'Two-Factor Authentication', color: 'bg-indigo-100 text-indigo-600' },
    ],
  },
  {
    title: 'Support',
    items: [
      { icon: HelpCircle, label: 'Help Center', color: 'bg-teal-100 text-teal-600' },
      { icon: FileText, label: 'Terms & Conditions', color: 'bg-cyan-100 text-cyan-600' },
      { icon: FileText, label: 'Privacy Policy', color: 'bg-pink-100 text-pink-600' },
    ],
  },
];

export function StudentSettingsScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Settings" showBack />

        <div className="p-4">
          {/* Account Info */}
          <div className="bg-white rounded-2xl shadow-sm p-4 mb-4">
            <div className="flex items-center gap-3">
              <div className="w-16 h-16 bg-gradient-to-br from-[#276C84] to-[#22C55E] rounded-full flex items-center justify-center">
                <span className="text-white text-xl font-bold">SJ</span>
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-[#1E293B] mb-1">Sarah Johnson</h3>
                <p className="text-sm text-gray-500">sarah.johnson@university.edu</p>
                <p className="text-xs text-gray-400 mt-1">Student ID: 2024001</p>
              </div>
            </div>
          </div>

          {/* Settings Sections */}
          {settingsSections.map((section, sectionIndex) => (
            <div key={sectionIndex} className="mb-6">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wide mb-3 px-1">
                {section.title}
              </h3>
              <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
                {section.items.map((item, itemIndex) => {
                  const Icon = item.icon;
                  return (
                    <button
                      key={itemIndex}
                      className={`
                        w-full flex items-center gap-3 p-4 hover:bg-gray-50 transition-colors
                        ${itemIndex !== section.items.length - 1 ? 'border-b border-gray-100' : ''}
                      `}
                    >
                      <div className={`${item.color} p-2 rounded-lg`}>
                        <Icon className="w-5 h-5" />
                      </div>
                      <div className="flex-1 text-left">
                        <p className="font-medium text-[#1E293B]">{item.label}</p>
                        {item.value && (
                          <p className="text-sm text-gray-500">{item.value}</p>
                        )}
                      </div>
                      <ChevronRight className="w-5 h-5 text-gray-400" />
                    </button>
                  );
                })}
              </div>
            </div>
          ))}

          {/* App Version */}
          <div className="text-center mt-8">
            <p className="text-sm text-gray-400">University Students</p>
            <p className="text-xs text-gray-400 mt-1">Version 1.0.0</p>
          </div>
        </div>

        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
