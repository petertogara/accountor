package com.accountor.prh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MainBusinessLineApiDto( @JsonProperty("type") String typeCode, List<DescriptionApiDto> descriptions) {
}