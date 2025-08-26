package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DescriptionEntryApiDto(String languageCode, String description) {
}
