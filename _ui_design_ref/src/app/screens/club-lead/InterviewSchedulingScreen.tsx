import { Calendar, Clock, User } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function InterviewSchedulingScreen() {
  const interviews = [
    { id: 1, candidate: 'Sarah Johnson', date: '2024-03-25', time: '10:00 AM', status: 'scheduled' },
    { id: 2, candidate: 'Michael Chen', date: '2024-03-25', time: '11:00 AM', status: 'scheduled' },
    { id: 3, candidate: 'Emma Williams', date: '2024-03-26', time: '2:00 PM', status: 'pending' },
  ];

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Interview Schedule" showBack />
        <div className="px-4 py-6 space-y-4">
          {interviews.map((interview) => (
            <div key={interview.id} className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-[#8B5CF6]/10 rounded-full flex items-center justify-center">
                    <User className="w-5 h-5 text-[#8B5CF6]" />
                  </div>
                  <div>
                    <h4 className="font-semibold text-[#1E293B]">{interview.candidate}</h4>
                    <p className="text-xs text-gray-500">{interview.status}</p>
                  </div>
                </div>
              </div>
              <div className="space-y-2">
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Calendar className="w-4 h-4" />
                  {new Date(interview.date).toLocaleDateString()}
                </div>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Clock className="w-4 h-4" />
                  {interview.time}
                </div>
              </div>
              <button className="w-full mt-3 bg-[#8B5CF6] text-white py-2 rounded-lg text-sm font-medium hover:bg-[#7C3AED]">
                Reschedule
              </button>
            </div>
          ))}
        </div>
        <BottomNav role="club-lead" />
      </div>
    </MobileContainer>
  );
}
