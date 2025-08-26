export interface CompanyDetailsDto {
    businessId: string;
    name: string;
    registrationDate: string;
    websiteUrl: string | null;
    street: string | null;
    city: string | null;
    postalCode: string | null;
    mainBusinessLineCode: string | null;
    mainBusinessLineDescription: string | null;
}