import { BarChart3, TrendingUp, Download, Calendar, Award } from 'lucide-react';
import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';

export function ReportsScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Reports & Analytics" showBack showNotifications />

        <div className="px-4 py-6 space-y-6">
          {/* Summary Statistics */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Quick Stats</h3>
            <div className="grid grid-cols-2 gap-3">
              <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
                <div className="bg-blue-100 w-10 h-10 rounded-xl flex items-center justify-center mb-3">
                  <TrendingUp className="w-5 h-5 text-blue-600" />
                </div>
                <p className="text-2xl font-bold text-[#1E293B] mb-1">94%</p>
                <p className="text-xs text-gray-600 mb-1">Enrollment Rate</p>
                <span className="text-xs text-green-600 font-semibold">+8% ↑</span>
              </div>
              <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
                <div className="bg-green-100 w-10 h-10 rounded-xl flex items-center justify-center mb-3">
                  <BarChart3 className="w-5 h-5 text-green-600" />
                </div>
                <p className="text-2xl font-bold text-[#1E293B] mb-1">87%</p>
                <p className="text-xs text-gray-600 mb-1">Event Success</p>
                <span className="text-xs text-green-600 font-semibold">+12% ↑</span>
              </div>
              <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
                <div className="bg-purple-100 w-10 h-10 rounded-xl flex items-center justify-center mb-3">
                  <Calendar className="w-5 h-5 text-purple-600" />
                </div>
                <p className="text-2xl font-bold text-[#1E293B] mb-1">28</p>
                <p className="text-xs text-gray-600">Active Clubs</p>
              </div>
              <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100">
                <div className="bg-orange-100 w-10 h-10 rounded-xl flex items-center justify-center mb-3">
                  <Award className="w-5 h-5 text-orange-600" />
                </div>
                <p className="text-2xl font-bold text-[#1E293B] mb-1">4.6/5</p>
                <p className="text-xs text-gray-600">Satisfaction</p>
              </div>
            </div>
          </div>

          {/* Chart Placeholders */}
          <div>
            <h3 className="text-lg font-semibold text-[#1E293B] mb-3">Analytics</h3>
            
            {/* Enrollment Trend */}
            <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 mb-3">
              <div className="flex items-center justify-between mb-4">
                <h4 className="font-semibold text-[#1E293B]">Student Enrollment Trend</h4>
                <button className="text-xs text-[#276C84] font-semibold bg-[#276C84]/10 px-3 py-1.5 rounded-lg">
                  This Year
                </button>
              </div>
              <div className="h-40 bg-gradient-to-t from-[#276C84]/10 to-transparent rounded-xl flex items-end justify-around p-4">
                {[65, 78, 85, 92, 88, 94].map((height, i) => (
                  <div
                    key={i}
                    className="w-8 bg-gradient-to-t from-[#276C84] to-[#1f5668] rounded-t-lg"
                    style={{ height: `${height}%` }}
                  ></div>
                ))}
              </div>
              <div className="flex justify-around mt-3">
                {['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'].map((month) => (
                  <span key={month} className="text-xs text-gray-500 font-medium">{month}</span>
                ))}
              </div>
            </div>

            {/* Event Participation */}
            <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100">
              <div className="flex items-center justify-between mb-4">
                <h4 className="font-semibold text-[#1E293B]">Event Participation</h4>
                <button className="text-xs text-[#276C84] font-semibold bg-[#276C84]/10 px-3 py-1.5 rounded-lg">
                  This Month
                </button>
              </div>
              <div className="space-y-4">
                {[
                  { name: 'Tech Events', percentage: 85, color: '#276C84' },
                  { name: 'Cultural Events', percentage: 72, color: '#8B5CF6' },
                  { name: 'Sports Events', percentage: 68, color: '#22C55E' },
                  { name: 'Academic Events', percentage: 91, color: '#F59E0B' },
                ].map((item) => (
                  <div key={item.name}>
                    <div className="flex justify-between text-sm mb-2">
                      <span className="text-gray-600 font-medium">{item.name}</span>
                      <span className="font-bold text-[#1E293B]">{item.percentage}%</span>
                    </div>
                    <div className="h-2.5 bg-gray-100 rounded-full overflow-hidden">
                      <div
                        className="h-full rounded-full transition-all"
                        style={{ width: `${item.percentage}%`, backgroundColor: item.color }}
                      ></div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Export Button */}
          <button className="w-full bg-gradient-to-r from-[#276C84] to-[#1f5668] text-white py-3.5 rounded-xl font-semibold hover:from-[#1f5668] hover:to-[#18495a] transition-all flex items-center justify-center gap-2 shadow-sm">
            <Download className="w-5 h-5" />
            Export Full Report
          </button>
        </div>

        <BottomNav role="admin" />
      </div>
    </MobileContainer>
  );
}