package com.webling.graincorp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

@Table(database = GrainCorpDatabase.class)
public class MenuChildItem extends BaseModel {

    @PrimaryKey
    @SerializedName("Id")
    @Expose
    private String id;

    @Column
    @SerializedName("ParentId")
    @Expose
    private String parentId;

    @Column
    @SerializedName("Title")
    @Expose
    private String title;

    @Column
    @SerializedName("Url")
    @Expose
    private String url;

    @Column
    @SerializedName("IntExtInd")
    @Expose
    private Boolean intExtInd;

    public MenuChildItem() {
    }

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

    public Boolean isIntExtInd() {
        return intExtInd;
    }

    public void setIntExtInd(Boolean intExtInd) {
        this.intExtInd = intExtInd;
    }
}
