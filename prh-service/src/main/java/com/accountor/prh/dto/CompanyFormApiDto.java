package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyFormApiDto(
        String type,
        List<DescriptionApiDto> descriptions,
        @JsonProperty("endDate") LocalDate endDate,
        int version,
        String source) {
}