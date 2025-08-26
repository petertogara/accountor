import React, { useState, type FC, type ChangeEvent, type KeyboardEvent } from 'react';
import type {CompanyDetailsDto} from '../types/CompanyDetailsDto.ts';

interface CompanySearchProps {
    setCompanyData: React.Dispatch<React.SetStateAction<CompanyDetailsDto | null>>;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

const CompanySearch: FC<CompanySearchProps> = ({ setCompanyData, setLoading, setError }) => {
    const [businessIdInput, setBusinessIdInput] = useState<string>('');
    const [inputError, setInputError] = useState<string>('');

    const BASE_API_PATH: string = '/api/v1/prh/companies/';

    /**
     * Validates the business ID input.
     * @param {string} id - The business ID to validate.
     * @returns {boolean} - True if valid, false otherwise.
     */
    const validateBusinessId = (id: string): boolean => {
        if (!id || id.trim() === '') {
            setInputError('Business ID cannot be empty.');
            return false;
        }
        // Basic format check: should contain numbers and a hyphen (e.g., 0100002-9)
        if (!/^\d{7}-\d$/.test(id)) {
            setInputError('Invalid Business ID format. Expected format: 1234567-8');
            return false;
        }
        setInputError('');
        return true;
    };

    /**
     * Handles the search action when the button is clicked or Enter is pressed.
     * Fetches company data from the backend API.
     */
    const handleSearch = async (): Promise<void> => {
        setCompanyData(null);
        setError(null);

        if (!validateBusinessId(businessIdInput)) {
            return;
        }

        setLoading(true);

        try {

            const response = await fetch(`${BASE_API_PATH}${businessIdInput.trim()}`);

            if (!response.ok) {

                const errorBody = await response.text();
                let errorMessage = `HTTP error! Status: ${response.status}`;
                try {
                    const errorJson = JSON.parse(errorBody);
                    errorMessage = errorJson.message || errorMessage;
                    // eslint-disable-next-line @typescript-eslint/no-unused-vars
                } catch (jsonParseError) {
                    errorMessage = errorBody || response.statusText || errorMessage;
                }
                throw new Error(errorMessage);
            }


            const data: CompanyDetailsDto = await response.json();

            if (data && data.businessId) {
                setCompanyData(data);
            } else {
                setError(`No company found with Business ID: ${businessIdInput}.`);
                setCompanyData(null);
            }
        } catch (err: unknown) {
            let errorMessage = "An unknown error occurred."; // Default error message

            if (err instanceof Error) {
                errorMessage = err.message;
            } else if (typeof err === 'string') {
                errorMessage = err;
            } else if (err && typeof err === 'object' && 'message' in err) {
                errorMessage = (err as { message: string }).message;
            }

            setError(`Failed to fetch company data: ${errorMessage}. Please check the Business ID and your network connection. If using Docker, ensure Nginx proxy is correctly configured for HTTPS backend (localhost:9443).`);
            setCompanyData(null);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-7xl bg-white rounded-lg shadow-lg p-6 sm:p-8 flex flex-col items-center mb-8"> {/* Corrected max-w to 5xl */}
            <div className="w-full mb-4">
                <label htmlFor="businessId" className="block text-gray-700 text-sm font-bold mb-2">
                    Enter Business ID:
                </label>
                <input
                    type="text"
                    id="businessId"
                    className="shadow appearance-none border rounded-lg w-full py-3 px-4 text-gray-300 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200 ease-in-out"
                    placeholder="e.g., 0100002-9"
                    value={businessIdInput}
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setBusinessIdInput(e.target.value)}
                    onKeyPress={(e: KeyboardEvent<HTMLInputElement>) => {
                        if (e.key === 'Enter') {
                            handleSearch();
                        }
                    }}
                    aria-label="Search Company"
                />
                {inputError && <p className="text-red-500 text-xs mt-2">{inputError}</p>}
            </div>
            <button
                onClick={handleSearch}
                className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75"
                aria-label="Search Company"
            >
                Search Company
            </button>
        </div>
    );
};

export default CompanySearch;