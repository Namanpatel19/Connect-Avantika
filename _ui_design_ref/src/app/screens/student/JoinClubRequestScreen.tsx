import { useState } from 'react';
import { FileText, Send } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function JoinClubRequestScreen() {
  const [reason, setReason] = useState('');
  const [skills, setSkills] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Handle submission
  };

  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Join Club Request" showBack />
        <div className="px-4 py-6">
          <div className="bg-white rounded-xl p-4 shadow-sm mb-6">
            <h3 className="font-semibold text-[#1E293B] mb-2">Tech Club</h3>
            <p className="text-sm text-gray-600">Complete this form to request membership</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Why do you want to join?
              </label>
              <div className="relative">
                <FileText className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
                <textarea
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  placeholder="Tell us about your interest..."
                  rows={4}
                  className="w-full pl-11 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#22C55E]"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Relevant Skills/Experience
              </label>
              <div className="relative">
                <FileText className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
                <textarea
                  value={skills}
                  onChange={(e) => setSkills(e.target.value)}
                  placeholder="Share your skills and experience..."
                  rows={4}
                  className="w-full pl-11 pr-4 py-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#22C55E]"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-[#22C55E] text-white py-3.5 rounded-xl font-semibold hover:bg-[#16A34A] flex items-center justify-center gap-2 shadow-lg"
            >
              <Send className="w-5 h-5" />
              Submit Request
            </button>
          </form>
        </div>
        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
