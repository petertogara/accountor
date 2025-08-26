import {type FC } from 'react';
import type {CompanyDetailsDto} from '../types/CompanyDetailsDto.ts';


interface CompanyDetailsProps {
    companyData: CompanyDetailsDto;
}

const CompanyDetails: FC<CompanyDetailsProps> = ({ companyData }) => {
    if (!companyData) {
        return null;
    }

    return (
        <div className="w-full bg-white rounded-lg shadow-lg p-6 sm:p-8 mt-8"> {/* Corrected max-w to 7xl */}
            <h2 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-6 border-b pb-3">Company Details</h2>

            {/* Business ID */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Business ID:</p>
                <p className="text-gray-900 text-xl break-words">{companyData.businessId || 'N/A'}</p>
            </div>

            {/* Company Name */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Company Name:</p>
                <p className="text-gray-900 text-xl break-words">{companyData.name || 'N/A'}</p>
            </div>

            {/* Registration Date */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Registration Date:</p>
                <p className="text-gray-900 text-lg">{companyData.registrationDate || 'N/A'}</p>
            </div>

            {/* Website */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Website:</p>
                {companyData.websiteUrl ? (
                    <a
                        href={companyData.websiteUrl.startsWith('http') ? companyData.websiteUrl : `http://${companyData.websiteUrl}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:underline text-lg break-words"
                    >
                        {companyData.websiteUrl}
                    </a>
                ) : (
                    <p className="text-gray-900 text-lg">N/A</p>
                )}
            </div>

            {/* Address */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Address:</p>
                {companyData.street && companyData.city && companyData.postalCode ? (
                    <div className="text-gray-900 text-lg">
                        <p>{companyData.street}</p>
                        <p>{companyData.postalCode} {companyData.city}</p>
                    </div>
                ) : (
                    <p className="text-gray-900 text-lg">N/A</p>
                )}
            </div>

            {/* Main Business Line */}
            <div className="mb-4">
                <p className="text-gray-600 font-semibold text-lg">Main Line of Business:</p>
                {companyData.mainBusinessLineCode && companyData.mainBusinessLineDescription ? (
                    <div className="text-gray-900 text-lg">
                        <p className="break-words"><strong>Code:</strong> {companyData.mainBusinessLineCode}</p>
                        <p className="break-words"><strong>Description:</strong> {companyData.mainBusinessLineDescription}</p>
                    </div>
                ) : (
                    <p className="text-gray-900 text-lg">N/A</p>
                )}
            </div>
        </div>
    );
};

export default CompanyDetails;
