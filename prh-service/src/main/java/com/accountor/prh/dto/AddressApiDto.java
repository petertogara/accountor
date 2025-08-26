package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressApiDto(
        @JsonProperty("type") String type,
        @JsonProperty("street") String street,
        @JsonProperty("postCode") String postCode,
        @JsonProperty("postOffices") List<PostOfficeApiDto> postOffices) {
}
