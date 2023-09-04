
package com.webling.graincorp.data.api.model.response.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Child {
    @SerializedName("ParentId")
    @Expose
    private String parentId;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("IntExtInd")
    @Expose
    private Boolean intExtInd;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIntExtInd() {
        return intExtInd;
    }

    public void setIntExtInd(Boolean intExtInd) {
        this.intExtInd = intExtInd;
    }
}
