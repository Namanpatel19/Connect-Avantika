import { Bell, CheckCircle, XCircle, Calendar } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ClubNotificationsScreen() {
  const notifications = [
    { id: 1, title: 'Event Approved', message: 'Your event "Tech Symposium" has been approved', time: '2 hours ago', type: 'success', icon: CheckCircle },
    { id: 2, title: 'New Member Request', message: 'Sarah Johnson wants to join your club', time: '5 hours ago', type: 'info', icon: Bell },
    { id: 3, title: 'Event Rejected', message: 'Your event "AI Workshop" was rejected', time: '1 day ago', type: 'error', icon: XCircle },
    { id: 4, title: 'Upcoming Event', message: 'Coding Workshop starts in 2 days', time: '1 day ago', type: 'warning', icon: Calendar },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Notifications" showBack />
        <div className="px-4 py-6 space-y-3">
          {notifications.map((notif) => {
            const Icon = notif.icon;
            const colorMap = { success: 'green', info: 'blue', error: 'red', warning: 'yellow' };
            const color = colorMap[notif.type as keyof typeof colorMap];
            return (
              <div key={notif.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
                <div className="flex items-start gap-3">
                  <div className={`w-10 h-10 bg-${color}-100 rounded-full flex items-center justify-center flex-shrink-0`}>
                    <Icon className={`w-5 h-5 text-${color}-600`} />
                  </div>
                  <div className="flex-1">
                    <h4 className="font-semibold text-[#1E293B] mb-1">{notif.title}</h4>
                    <p className="text-sm text-gray-600 mb-2">{notif.message}</p>
                    <p className="text-xs text-gray-400">{notif.time}</p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}
