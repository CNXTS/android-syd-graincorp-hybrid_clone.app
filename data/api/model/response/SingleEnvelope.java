
package com.webling.graincorp.data.api.model.response;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class SingleEnvelope<T> {

    @SerializedName("d")
    T object;

    public T getObject() {
        return object;
    }

}
