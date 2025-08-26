package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyApiDto(
        BusinessIdApiDto businessId,
        List<NameApiDto> names,
        @JsonProperty("addresses")List<AddressApiDto> addresses,
        @JsonProperty("registrationDate") LocalDate registrationDate,
        @JsonProperty("endDate") LocalDate endDate,
        @JsonProperty("companyForms") List<CompanyFormApiDto> companyForms,
        @JsonProperty("companySituations") List<CompanySituationApiDto> companySituations,
        @JsonProperty("tradeRegisterStatus") String tradeRegisterStatus,
        @JsonProperty("mainBusinessLine") MainBusinessLineApiDto mainBusinessLine,
        @JsonProperty("registeredEntries") List<RegisteredEntryApiDto> registeredEntries,
        Optional<WebsiteApiDto> website
) {
}