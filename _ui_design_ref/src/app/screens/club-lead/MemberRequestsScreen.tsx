import { CheckCircle, XCircle, Phone, Mail, User, Award } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function MemberRequestsScreen() {
  const requests = [
    { id: 1, name: 'Sarah Johnson', email: 'sarah.j@uni.edu', phone: '+1 234-567-8901', department: 'Computer Science', year: '2nd Year', date: '2024-03-22' },
    { id: 2, name: 'Michael Chen', email: 'michael.c@uni.edu', phone: '+1 234-567-8902', department: 'Engineering', year: '3rd Year', date: '2024-03-21' },
    { id: 3, name: 'Emma Williams', email: 'emma.w@uni.edu', phone: '+1 234-567-8903', department: 'Business', year: '1st Year', date: '2024-03-20' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Member Requests" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Pending Count */}
          <div className="bg-gradient-to-r from-purple-50 to-pink-50 border border-purple-200 rounded-2xl p-4 flex items-center gap-3">
            <div className="bg-purple-100 p-2.5 rounded-xl">
              <Award className="w-5 h-5 text-purple-600" />
            </div>
            <div>
              <p className="text-sm font-semibold text-purple-900">
                {requests.length} new membership request{requests.length !== 1 ? 's' : ''}
              </p>
              <p className="text-xs text-purple-700">Review and take action</p>
            </div>
          </div>

          {/* Requests List */}
          <div className="space-y-3">
            <h3 className="text-lg font-semibold text-[#1E293B]">Pending Requests</h3>
            {requests.map((request) => (
              <div key={request.id} className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 hover:shadow-md transition-all">
                <div className="flex items-start gap-3 mb-3">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#8B5CF6] to-[#7C3AED] rounded-full flex items-center justify-center flex-shrink-0">
                    <User className="w-6 h-6 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <h4 className="font-semibold text-[#1E293B] mb-1">{request.name}</h4>
                    <p className="text-sm text-gray-600 mb-1">{request.department}</p>
                    <p className="text-xs text-[#8B5CF6] font-medium">{request.year}</p>
                  </div>
                </div>

                <div className="bg-gray-50 rounded-lg p-3 mb-3 space-y-1.5">
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Mail className="w-3.5 h-3.5 text-gray-400" />
                    {request.email}
                  </div>
                  <div className="flex items-center gap-2 text-xs text-gray-600">
                    <Phone className="w-3.5 h-3.5 text-gray-400" />
                    {request.phone}
                  </div>
                  <p className="text-xs text-gray-500 pt-1">
                    Requested: {new Date(request.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </p>
                </div>

                <div className="grid grid-cols-3 gap-2">
                  <button className="bg-green-600 text-white py-2.5 rounded-xl text-xs font-semibold hover:bg-green-700 transition-colors flex items-center justify-center gap-1 shadow-sm">
                    <CheckCircle className="w-4 h-4" />
                    Accept
                  </button>
                  <button className="bg-red-600 text-white py-2.5 rounded-xl text-xs font-semibold hover:bg-red-700 transition-colors flex items-center justify-center gap-1 shadow-sm">
                    <XCircle className="w-4 h-4" />
                    Reject
                  </button>
                  <button className="bg-[#8B5CF6] text-white py-2.5 rounded-xl text-xs font-semibold hover:bg-[#7C3AED] transition-colors flex items-center justify-center gap-1 shadow-sm">
                    <Phone className="w-4 h-4" />
                    Interview
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}