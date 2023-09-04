package com.webling.graincorp.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.util.UUID;

@Table(database = GrainCorpDatabase.class)
public class SearchFilter extends BaseModel implements Comparable<SearchFilter> {
    @PrimaryKey
    private UUID id = UUID.randomUUID();
    @SerializedName("site")
    @Column
    String site;
    @SerializedName("commodity")
    @Column
    String commodity;
    @SerializedName("grade")
    @Column
    String grade;
    @SerializedName("siteDesc")
    @Column
    String siteDescription;
    @SerializedName("commodityDesc")
    @Column
    String commodityDescription;
    @Column
    long timestamp;

    public SearchFilter() {
        timestamp = System.currentTimeMillis();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public String getCommodityDescription() {
        return commodityDescription;
    }

    public void setCommodityDescription(String commodityDescription) {
        this.commodityDescription = commodityDescription;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(@NonNull SearchFilter o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
