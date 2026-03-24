import { MobileContainer } from '../../components/MobileContainer';
import { Header } from '../../components/Header';
import { BottomNav } from '../../components/BottomNav';
import { Bus, MapPin, Clock, Navigation } from 'lucide-react';

const mockBuses = [
  {
    id: 1,
    number: 'BUS 101',
    driver: 'John Smith',
    status: 'On Route',
    statusColor: 'bg-green-100 text-green-700 border-green-200',
    eta: '5 min',
    route: 'Main Campus - North Dorms',
    currentLocation: 'Near Library',
  },
  {
    id: 2,
    number: 'BUS 102',
    driver: 'Sarah Williams',
    status: 'Arriving',
    statusColor: 'bg-blue-100 text-blue-700 border-blue-200',
    eta: '2 min',
    route: 'Main Campus - South Dorms',
    currentLocation: 'Engineering Building',
  },
  {
    id: 3,
    number: 'BUS 103',
    driver: 'Mike Johnson',
    status: 'Delayed',
    statusColor: 'bg-red-100 text-red-700 border-red-200',
    eta: '15 min',
    route: 'Main Campus - City Center',
    currentLocation: 'Traffic on Main St',
  },
  {
    id: 4,
    number: 'BUS 104',
    driver: 'Emily Davis',
    status: 'On Route',
    statusColor: 'bg-green-100 text-green-700 border-green-200',
    eta: '8 min',
    route: 'Sports Complex - Main Campus',
    currentLocation: 'Stadium Parking',
  },
];

export function TransportScreen() {
  return (
    <MobileContainer>
      <div className="min-h-screen bg-[#F8FAFC] pb-20">
        <Header title="Campus Transport" showBack />

        <div className="p-4">
          {/* Info Banner */}
          <div className="bg-gradient-to-r from-[#276C84] to-[#22C55E] rounded-2xl p-4 mb-4 shadow-lg">
            <div className="flex items-center gap-3 text-white">
              <div className="bg-white/20 p-2 rounded-lg">
                <Bus className="w-6 h-6" />
              </div>
              <div>
                <h3 className="font-semibold text-lg">Live Bus Tracking</h3>
                <p className="text-sm text-white/90">Track campus buses in real-time</p>
              </div>
            </div>
          </div>

          {/* Buses List */}
          <div className="space-y-3">
            {mockBuses.map((bus) => (
              <div key={bus.id} className="bg-white rounded-2xl shadow-sm overflow-hidden">
                {/* Bus Header */}
                <div className="p-4 bg-gradient-to-r from-gray-50 to-white border-b">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-3">
                      <div className="bg-[#276C84] p-2.5 rounded-xl">
                        <Bus className="w-5 h-5 text-white" />
                      </div>
                      <div>
                        <h4 className="font-bold text-[#1E293B]">{bus.number}</h4>
                        <p className="text-xs text-gray-500">{bus.driver}</p>
                      </div>
                    </div>
                    <div className={`px-3 py-1.5 rounded-full border-2 text-xs font-semibold ${bus.statusColor}`}>
                      {bus.status}
                    </div>
                  </div>
                </div>

                {/* Bus Details */}
                <div className="p-4">
                  <div className="space-y-3">
                    {/* Route */}
                    <div className="flex items-start gap-3">
                      <div className="bg-purple-100 p-2 rounded-lg mt-0.5">
                        <MapPin className="w-4 h-4 text-purple-600" />
                      </div>
                      <div className="flex-1">
                        <p className="text-xs text-gray-500 mb-1">Route</p>
                        <p className="text-sm font-medium text-[#1E293B]">{bus.route}</p>
                      </div>
                    </div>

                    {/* Current Location */}
                    <div className="flex items-start gap-3">
                      <div className="bg-orange-100 p-2 rounded-lg mt-0.5">
                        <Navigation className="w-4 h-4 text-orange-600" />
                      </div>
                      <div className="flex-1">
                        <p className="text-xs text-gray-500 mb-1">Current Location</p>
                        <p className="text-sm font-medium text-[#1E293B]">{bus.currentLocation}</p>
                      </div>
                    </div>

                    {/* ETA */}
                    <div className="flex items-start gap-3">
                      <div className="bg-blue-100 p-2 rounded-lg mt-0.5">
                        <Clock className="w-4 h-4 text-blue-600" />
                      </div>
                      <div className="flex-1">
                        <p className="text-xs text-gray-500 mb-1">Estimated Arrival</p>
                        <p className="text-sm font-semibold text-[#276C84]">{bus.eta}</p>
                      </div>
                    </div>
                  </div>

                  {/* Live Location Button */}
                  <button className="w-full mt-4 bg-[#276C84] text-white py-3 rounded-xl font-medium flex items-center justify-center gap-2 hover:bg-[#1f5668] transition-all shadow-md hover:shadow-lg">
                    <MapPin className="w-4 h-4" />
                    View Live Location
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Legend */}
          <div className="mt-6 bg-white rounded-2xl p-4 shadow-sm">
            <h4 className="font-semibold text-[#1E293B] mb-3 text-sm">Status Legend</h4>
            <div className="grid grid-cols-3 gap-3 text-center">
              <div>
                <div className="bg-green-100 text-green-700 border-2 border-green-200 px-2 py-1 rounded-lg text-xs font-semibold mb-1">
                  On Route
                </div>
                <p className="text-xs text-gray-500">Running normally</p>
              </div>
              <div>
                <div className="bg-blue-100 text-blue-700 border-2 border-blue-200 px-2 py-1 rounded-lg text-xs font-semibold mb-1">
                  Arriving
                </div>
                <p className="text-xs text-gray-500">Almost here</p>
              </div>
              <div>
                <div className="bg-red-100 text-red-700 border-2 border-red-200 px-2 py-1 rounded-lg text-xs font-semibold mb-1">
                  Delayed
                </div>
                <p className="text-xs text-gray-500">Running late</p>
              </div>
            </div>
          </div>
        </div>

        <BottomNav role="student" />
      </div>
    </MobileContainer>
  );
}
