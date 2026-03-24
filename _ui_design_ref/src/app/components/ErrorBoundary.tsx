import { useRouteError, isRouteErrorResponse } from 'react-router';
import { MobileContainer } from './MobileContainer';

export function ErrorBoundary() {
  const error = useRouteError();

  if (isRouteErrorResponse(error)) {
    return (
      <MobileContainer>
        <div className="min-h-screen flex flex-col items-center justify-center bg-[#F8FAFC] px-6">
          <div className="text-center">
            <h1 className="text-6xl font-bold text-[#276C84] mb-4">{error.status}</h1>
            <p className="text-xl text-gray-700 mb-2">{error.statusText}</p>
            {error.data && (
              <p className="text-gray-500 text-sm">{error.data}</p>
            )}
            <button
              onClick={() => window.location.href = '/'}
              className="mt-6 px-6 py-3 bg-[#276C84] text-white rounded-xl font-semibold hover:bg-[#1f5668]"
            >
              Go Home
            </button>
          </div>
        </div>
      </MobileContainer>
    );
  }

  return (
    <MobileContainer>
      <div className="min-h-screen flex flex-col items-center justify-center bg-[#F8FAFC] px-6">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-[#276C84] mb-4">Oops!</h1>
          <p className="text-xl text-gray-700 mb-4">Something went wrong</p>
          <button
            onClick={() => window.location.href = '/'}
            className="mt-6 px-6 py-3 bg-[#276C84] text-white rounded-xl font-semibold hover:bg-[#1f5668]"
          >
            Go Home
          </button>
        </div>
      </div>
    </MobileContainer>
  );
}
