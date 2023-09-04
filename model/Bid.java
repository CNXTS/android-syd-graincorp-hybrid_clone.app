
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
public class Bid extends BaseModel implements Comparable<Bid> {

    @PrimaryKey
    private UUID id = UUID.randomUUID();
    @SerializedName("Commodity")
    @Column
    private String commodity;
    @SerializedName("CommodityDesc")
    @Column
    private String commodityDesc;
    @SerializedName("Currency")
    @Column
    private String currency;
    @SerializedName("CustomerId")
    @Column
    private String customerId;
    @SerializedName("CustomerName")
    @Column
    private String customerName;
    @SerializedName("Grade")
    @Column
    private String grade;
    @SerializedName("PreferredBuyer")
    @Column
    private boolean preferredBuyer;
    @SerializedName("Price")
    @Column
    private String price;
    @SerializedName("Pricechange")
    @Column
    private String priceChange;
    @SerializedName("SeasonYear")
    @Column
    private String seasonYear;
    @SerializedName("SeasonYearDesc")
    @Column
    private String seasonYearDesc;
    @SerializedName("SiteName")
    @Column
    private String siteName;
    @SerializedName("SiteNo")
    @Column
    private String siteNo;
    @Column
    private long timestamp;
    @Column
    private boolean errorState;

    public Bid() {
        timestamp = System.currentTimeMillis();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCommodityDesc() {
        return commodityDesc;
    }

    public void setCommodityDesc(String commodityDesc) {
        this.commodityDesc = commodityDesc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isPreferredBuyer() {
        return preferredBuyer;
    }

    public void setPreferredBuyer(boolean preferredBuyer) {
        this.preferredBuyer = preferredBuyer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public String getSeasonYear() {
        return seasonYear;
    }

    public void setSeasonYear(String seasonYear) {
        this.seasonYear = seasonYear;
    }

    public String getSeasonYearDesc() {
        return seasonYearDesc;
    }

    public void setSeasonYearDesc(String seasonYearDesc) {
        this.seasonYearDesc = seasonYearDesc;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isErrorState() {
        return errorState;
    }

    public void setErrorState(boolean errorState) {
        this.errorState = errorState;
    }

    @Override
    public int compareTo(@NonNull Bid o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
