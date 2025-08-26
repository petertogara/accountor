package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NameApiDto(String name, String type, int version) {
}
