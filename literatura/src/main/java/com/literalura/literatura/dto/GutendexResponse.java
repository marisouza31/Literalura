package com.literalura.literatura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GutendexResponse {
    private List<GutendexBook> results;

    public List<GutendexBook> getResults() {
        return results;
    }

    public void setResults(List<GutendexBook> results) {
        this.results = results;
    }
}
