package com.accountor.prh.domain;

import java.time.LocalDate;

public record CompanyDetails(
        String businessId,
        String name,
        LocalDate registrationDate,
        String websiteUrl,
        String street,
        String city,
        String postalCode,
        String mainBusinessLineCode,
        String mainBusinessLineDescription) {
}