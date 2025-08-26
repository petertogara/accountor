package com.accountor.prh.domain;

import com.accountor.prh.dto.*;

import java.time.LocalDate;
import java.util.Optional;

public final class CompanyMapper {

    private CompanyMapper() {}

    /**
     * Maps the raw API DTO to our internal domain model.
     *
     * @param apiDto The DTO received from the external API.
     * @return Our clean domain model.
     */

    public static CompanyDetails toCompanyDetails(CompanyApiDto apiDto) {
        String currentName = apiDto.names().stream()
                .filter(nameDto -> "1".equals(nameDto.type()))
                .map(NameApiDto::name)
                .findFirst()
                .orElse(null);

        String websiteUrl = apiDto.website()
                .map(WebsiteApiDto::url)
                .orElse(null);

        String street = apiDto.addresses().stream()
                .map(AddressApiDto::street)
                .findFirst()
                .orElse(null);

        String city = apiDto.addresses().stream()
                .map(AddressApiDto::street)
                .findFirst()
                .orElse(null);

        String postCode = apiDto.addresses().stream()
                .map(AddressApiDto::postCode)
                .findFirst()
                .orElse(null);

        String mainBusinessLineCode = Optional.ofNullable(apiDto.mainBusinessLine())
                .map(MainBusinessLineApiDto::typeCode)
                .orElse(null);

        String mainBusinessLineDescription = Optional.ofNullable(apiDto.mainBusinessLine())
                .flatMap(line -> line.descriptions().stream()
                        .filter(desc -> "3".equals(desc.languageCode())) // "3" for English
                        .map(DescriptionApiDto::description)
                        .findFirst())
                .orElse(null);

        return new CompanyDetails(
                apiDto.businessId().value(),
                currentName,
                apiDto.registrationDate(),
                websiteUrl,
                street,
                city,
                postCode,
                mainBusinessLineCode,
                mainBusinessLineDescription
        );
    }

    /**
     * Maps our internal domain model to the DTO for API responses.
     *
     * @param domain The internal domain model.
     * @return The DTO to be returned by our REST API.
     */
    public static CompanyDetailsDto toCompanyDetailsDto(CompanyDetails domain) {
        return new CompanyDetailsDto(
                domain.businessId(),
                domain.name(),
                domain.registrationDate(),
                domain.websiteUrl(),
                domain.street(),
                domain.city(),
                domain.postalCode(),
                domain.mainBusinessLineCode(),
                domain.mainBusinessLineDescription()
        );
    }


}