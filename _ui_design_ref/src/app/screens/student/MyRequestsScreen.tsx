import { Clock, CheckCircle, XCircle } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { StatusBadge } from '../../components/StatusBadge';

export function MyRequestsScreen() {
  const requests = [
    { id: 1, type: 'Club Membership', club: 'Tech Club', date: '2024-03-22', status: 'pending' as const },
    { id: 2, type: 'Event Registration', event: 'Tech Symposium', date: '2024-03-20', status: 'accepted' as const },
    { id: 3, type: 'Club Membership', club: 'Arts Society', date: '2024-03-15', status: 'rejected' as const },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="My Requests" showBack />
        <div className="px-4 py-6 space-y-4">
          {requests.map((request) => {
            const iconMap = {
              pending: Clock,
              accepted: CheckCircle,
              rejected: XCircle,
            };
            const Icon = iconMap[request.status];
            const colorMap = {
              pending: 'yellow',
              accepted: 'green',
              rejected: 'red',
            };
            const color = colorMap[request.status];

            return (
              <div key={request.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
                <div className="flex items-start justify-between mb-3">
                  <div className="flex items-start gap-3 flex-1">
                    <div className={`w-10 h-10 bg-${color}-100 rounded-lg flex items-center justify-center flex-shrink-0`}>
                      <Icon className={`w-5 h-5 text-${color}-600`} />
                    </div>
                    <div className="flex-1">
                      <h4 className="font-semibold text-[#1E293B] mb-1">{request.type}</h4>
                      <p className="text-sm text-gray-600 mb-2">
                        {request.club || request.event}
                      </p>
                      <p className="text-xs text-gray-400">
                        Submitted on {new Date(request.date).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <StatusBadge status={request.status} />
                </div>
              </div>
            );
          })}
        </div>
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
