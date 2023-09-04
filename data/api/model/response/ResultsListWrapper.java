package com.webling.graincorp.data.api.model.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultsListWrapper<T> {

    @SerializedName("results")
    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
