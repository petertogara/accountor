package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessLineApiDto(String type, List<DescriptionEntryApiDto> descriptions) {
}