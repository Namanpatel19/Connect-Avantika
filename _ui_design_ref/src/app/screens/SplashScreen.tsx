import { useEffect } from 'react';
import { useNavigate } from 'react-router';
import { GraduationCap } from 'lucide-react';
import { MobileContainer } from '../components/MobileContainer';

export function SplashScreen() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('/login');
    }, 2000);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <MobileContainer>
      <div className="h-screen flex flex-col items-center justify-center bg-gradient-to-b from-[#276C84] to-[#1f5668] px-6">
        <div className="flex flex-col items-center gap-6 animate-fade-in">
          <div className="bg-white/10 backdrop-blur-sm p-6 rounded-3xl">
            <GraduationCap className="w-24 h-24 text-white" />
          </div>
          <div className="text-center">
            <h1 className="text-4xl font-bold text-white mb-2">
              University Students
            </h1>
            <p className="text-white/80 text-lg">
              Your Campus. Connected.
            </p>
          </div>
          <div className="mt-8">
            <div className="w-12 h-12 border-4 border-white/30 border-t-white rounded-full animate-spin"></div>
          </div>
        </div>
      </div>
    </MobileContainer>
  );
}
