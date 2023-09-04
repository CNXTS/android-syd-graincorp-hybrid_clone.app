
package com.webling.graincorp.data.api.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NestedEnvelope<T> {

    @SerializedName("d")
    @Expose
    private ResultsListWrapper<T> object;

    public ResultsListWrapper<T> getObject() {
        return object;
    }
}
