import { CheckCircle, XCircle, Calendar, MapPin, Users, Clock, Award } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { StatusBadge } from '../../components/StatusBadge';

export function EventApprovalScreen() {
  const eventRequests = [
    {
      id: 1,
      title: 'Tech Symposium 2024',
      club: 'Tech Club',
      clubIcon: '💻',
      date: '2024-04-15',
      time: '10:00 AM',
      venue: 'Main Auditorium',
      expectedAttendees: 200,
      status: 'pending' as const,
      description: 'Annual technology symposium featuring guest speakers from industry.',
    },
    {
      id: 2,
      title: 'Cultural Night',
      club: 'Arts & Culture Society',
      clubIcon: '🎭',
      date: '2024-04-20',
      time: '6:00 PM',
      venue: 'Campus Grounds',
      expectedAttendees: 500,
      status: 'pending' as const,
      description: 'Evening of cultural performances and traditional food stalls.',
    },
    {
      id: 3,
      title: 'Coding Workshop',
      club: 'Computer Science Club',
      clubIcon: '💻',
      date: '2024-04-10',
      time: '2:00 PM',
      venue: 'Lab B-201',
      expectedAttendees: 50,
      status: 'approved' as const,
      description: 'Hands-on workshop on modern web development.',
    },
  ];

  const pendingCount = eventRequests.filter(e => e.status === 'pending').length;

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Event Approvals" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Pending Count */}
          <div className="bg-gradient-to-r from-yellow-50 to-orange-50 border border-yellow-200 rounded-2xl p-4 flex items-center gap-3">
            <div className="bg-yellow-100 p-2.5 rounded-xl">
              <Award className="w-5 h-5 text-yellow-600" />
            </div>
            <div>
              <p className="text-sm font-semibold text-yellow-900">
                {pendingCount} event{pendingCount !== 1 ? 's' : ''} pending approval
              </p>
              <p className="text-xs text-yellow-700">Review and take action</p>
            </div>
          </div>

          {/* Event Requests */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">Event Requests</h3>
            {eventRequests.map((event) => (
              <div key={event.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#276C84] to-[#1f5668] rounded-xl flex items-center justify-center text-2xl flex-shrink-0">
                    {event.clubIcon}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <h4 className="font-semibold text-[#1E293B]">{event.title}</h4>
                      <StatusBadge status={event.status} />
                    </div>
                    <p className="text-sm text-gray-600 mb-2">{event.description}</p>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-lg p-3 mb-3 space-y-2">
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Users className="w-4 h-4 text-[#276C84]" />
                    <span className="font-medium">{event.club}</span>
                  </div>
                  <div className="grid grid-cols-2 gap-2">
                    <div className="flex items-center gap-2 text-xs text-gray-600">
                      <Calendar className="w-3.5 h-3.5 text-gray-400" />
                      {new Date(event.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </div>
                    <div className="flex items-center gap-2 text-xs text-gray-600">
                      <Clock className="w-3.5 h-3.5 text-gray-400" />
                      {event.time}
                    </div>
                  </div>
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <MapPin className="w-3.5 h-3.5 text-gray-400" />
                    {event.venue}
                  </div>
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Users className="w-3.5 h-3.5 text-gray-400" />
                    Expected: {event.expectedAttendees} attendees
                  </div>
                </div>

                {event.status === 'pending' && (
                  <div className="flex gap-2">
                    <button className="flex-1 bg-green-600 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-green-700 transition-colors flex items-center justify-center gap-1 shadow-sm">
                      <CheckCircle className="w-4 h-4" />
                      Approve
                    </button>
                    <button className="flex-1 bg-red-600 text-white py-2.5 rounded-xl text-sm font-semibold hover:bg-red-700 transition-colors flex items-center justify-center gap-1 shadow-sm">
                      <XCircle className="w-4 h-4" />
                      Reject
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}