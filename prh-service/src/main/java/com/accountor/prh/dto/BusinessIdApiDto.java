package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusinessIdApiDto(@JsonProperty("value") String value, String registrationDate) {
}