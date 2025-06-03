package com.memoritta.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TagSearchRequest {
    private List<String> tags;
    private boolean matchAll;
}
