package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DescriptionApiDto(
        @JsonProperty("languageCode") String languageCode,
        @JsonProperty("description") String description) {
}
