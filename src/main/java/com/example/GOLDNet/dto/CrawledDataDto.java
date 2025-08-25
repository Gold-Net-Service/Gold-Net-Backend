package com.example.GOLDNet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public record CrawledDataDto(
        @JsonProperty("상세내용")
        String detailContent,

        @JsonProperty("구")
        String district
) {
}
