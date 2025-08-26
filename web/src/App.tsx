import { useState, type FC } from 'react';
import CompanySearch from './components/CompanySearch.tsx';
import CompanyDetails from './components/CompanyDetails.tsx';
import type {CompanyDetailsDto} from './types/CompanyDetailsDto.ts';

const App: FC = () => {

    const [companyData, setCompanyData] = useState<CompanyDetailsDto | null>(null);

    const [loading, setLoading] = useState<boolean>(false);

    const [error, setError] = useState<string | null>(null);

    return (
        <div className="min-h-screen bg-gray-100 flex flex-col items-center p-4 sm:p-6 lg:p-8 font-inter">
            {/* Header where I put fleg on left */}
            <header className="w-full bg-white rounded-lg shadow-md p-6 mb-8 text-center">
                <h1 className="text-3xl sm:text-4xl font-bold text-gray-800 mb-2">Finnish Company Search 🇫🇮</h1>
                <p className="text-gray-600 text-lg">Search for company details using their Business ID.</p>
            </header>

            {/* Company Search Input Component */}
            <CompanySearch
                setCompanyData={setCompanyData}
                setLoading={setLoading}
                setError={setError}
            />

            {/* Loading Indicator */}
            {loading && (
                <div className="mt-8 p-4 bg-blue-100 text-blue-800 rounded-lg shadow-md animate-pulse">
                    Loading company data...
                </div>
            )}

            {/* Error Message Display */}
            {error && (
                <div className="mt-8 p-4 bg-red-100 text-red-800 rounded-lg shadow-md max-w-md text-center">
                    <p className="font-semibold mb-2">Error:</p>
                    <p>{error}</p>
                    {error.includes("Failed to fetch") && (
                        <p className="text-sm mt-2">
                            Please ensure your backend server is running on <code
                            className="font-mono">https://localhost:8888</code> and accessible.
                        </p>
                    )}
                </div>
            )}

            {/* Company Details Display Component */}
            {companyData && !loading && !error && (
                <CompanyDetails companyData={companyData}/>
            )}

            {/* Initial welcome message if no data, no loading, no error */}
            {!companyData && !loading && !error && (
                <div className="mt-6 text-gray-250 text-xl">
                    Enter a Business ID above to find company information.
                </div>
            )}
        </div>
    );
};

export default App;






