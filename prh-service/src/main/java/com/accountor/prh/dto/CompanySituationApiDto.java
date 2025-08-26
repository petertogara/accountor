package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanySituationApiDto(
        String type,
        List<DescriptionApiDto> descriptions,
        @JsonProperty("registrationDate") LocalDate registrationDate,
        @JsonProperty("endDate") LocalDate endDate,
        String register,
        String authority) {
}