package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResultApiDto(long totalResults, @JsonProperty("companies")List<CompanyApiDto> results) {
}
