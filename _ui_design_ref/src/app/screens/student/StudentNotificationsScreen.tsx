import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { Bell, Calendar, Users, Award, CheckCircle } from 'lucide-react';

const mockNotifications = [
  {
    id: 1,
    type: 'event',
    icon: Calendar,
    color: 'bg-blue-100 text-blue-600',
    title: 'New Event: Tech Club Meetup',
    description: 'Tech Club has scheduled a meetup for tomorrow at 2:00 PM',
    time: '2 hours ago',
    read: false,
  },
  {
    id: 2,
    type: 'club',
    icon: Users,
    color: 'bg-green-100 text-green-600',
    title: 'Club Request Accepted',
    description: 'Your request to join Basketball Club has been accepted',
    time: '5 hours ago',
    read: false,
  },
  {
    id: 3,
    type: 'achievement',
    icon: Award,
    color: 'bg-purple-100 text-purple-600',
    title: 'New Achievement Unlocked',
    description: 'You earned the "Event Enthusiast" badge',
    time: '1 day ago',
    read: true,
  },
  {
    id: 4,
    type: 'event',
    icon: Calendar,
    color: 'bg-blue-100 text-blue-600',
    title: 'Event Reminder',
    description: 'Basketball Tournament Finals starts in 3 days',
    time: '1 day ago',
    read: true,
  },
  {
    id: 5,
    type: 'club',
    icon: Users,
    color: 'bg-green-100 text-green-600',
    title: 'Interview Scheduled',
    description: 'Drama Club has scheduled an interview for March 27 at 3:00 PM',
    time: '2 days ago',
    read: true,
  },
  {
    id: 6,
    type: 'event',
    icon: Calendar,
    color: 'bg-blue-100 text-blue-600',
    title: 'Event Registration Confirmed',
    description: 'Your registration for Career Fair has been confirmed',
    time: '3 days ago',
    read: true,
  },
];

export function StudentNotificationsScreen() {
  const unreadCount = mockNotifications.filter(n => !n.read).length;

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Notifications" showBack />

        <div className="p-4">
          {/* Notifications Header */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Bell className="w-5 h-5 text-[#276C84]" />
              <h2 className="text-lg font-semibold text-[#1E293B]">
                All Notifications
              </h2>
            </div>
            {unreadCount > 0 && (
              <span className="bg-[#276C84] text-white text-xs font-semibold px-2.5 py-1 rounded-full">
                {unreadCount} new
              </span>
            )}
          </div>

          {/* Notifications List */}
          <div className="space-y-3">
            {mockNotifications.map(notification => {
              const Icon = notification.icon;
              return (
                <div
                  key={notification.id}
                  className={`
                    bg-white rounded-xl shadow-sm p-4 transition-all
                    ${!notification.read ? 'border-l-4 border-[#276C84]' : ''}
                  `}
                >
                  <div className="flex gap-3">
                    <div className={`${notification.color} p-2 rounded-lg h-fit`}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-start justify-between mb-1">
                        <h4 className={`font-semibold text-[#1E293B] ${!notification.read ? 'font-bold' : ''}`}>
                          {notification.title}
                        </h4>
                        {!notification.read && (
                          <div className="w-2 h-2 bg-[#276C84] rounded-full mt-1.5"></div>
                        )}
                      </div>
                      <p className="text-sm text-gray-600 mb-2">
                        {notification.description}
                      </p>
                      <div className="flex items-center justify-between">
                        <span className="text-xs text-gray-500">{notification.time}</span>
                        {notification.read && (
                          <div className="flex items-center gap-1 text-xs text-gray-400">
                            <CheckCircle className="w-3 h-3" />
                            <span>Read</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Empty State (hidden when there are notifications) */}
          {mockNotifications.length === 0 && (
            <div className="flex flex-col items-center justify-center py-12">
              <div className="bg-gray-100 p-6 rounded-full mb-4">
                <Bell className="w-12 h-12 text-gray-400" />
              </div>
              <h3 className="text-lg font-semibold text-[#1E293B] mb-2">
                No Notifications
              </h3>
              <p className="text-gray-500 text-center">
                You're all caught up! Check back later for updates.
              </p>
            </div>
          )}
        </div>

        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
