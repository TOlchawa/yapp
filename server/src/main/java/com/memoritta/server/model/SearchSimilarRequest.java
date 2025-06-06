package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class SearchSimilarRequest {
    private String id;
    private Map<String, String> parameters;
}
