package com.webling.graincorp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

@Table(database = GrainCorpDatabase.class)
public class DeviceSettings extends BaseModel {

    @PrimaryKey
    @Column(defaultValue = "1")
    private Integer id = 1;

    @SerializedName("PinStatus")
    @Expose
    @PinStatus
    @Column
    private String pinStatus;

    @SerializedName("PinExists")
    @Expose
    @Column
    private boolean pinExists;

    @SerializedName("PNCount")
    @Expose
    @Column
    private int pnCount;

    @SerializedName("Key")
    @Expose
    private String key; // DO NOT STORE THIS

    @SerializedName("OnboardUpd")
    @Expose
    @Column
    private boolean onboardingUpdated;

    @SerializedName("AppUpd")
    @Expose
    @Column
    private boolean appUpdateRequired;

    @SerializedName("AppUpdDesc")
    @Expose
    @Column
    private String appUpdateDescription;

    public DeviceSettings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @PinStatus
    public String getPinStatus() {
        return pinStatus;
    }

    public void setPinStatus(@PinStatus String pinStatus) {
        this.pinStatus = pinStatus;
    }

    public boolean isPinExists() {
        return pinExists;
    }

    public void setPinExists(boolean pinExists) {
        this.pinExists = pinExists;
    }

    public int getPnCount() {
        return pnCount;
    }

    public void setPnCount(int pnCount) {
        this.pnCount = pnCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isPinSetupForDevice() {
        return pinExists && pinStatus.equals(PinStatus.ACTIVE);
    }

    public boolean isOnboardingUpdated() {
        return onboardingUpdated;
    }

    public void setOnboardingUpdated(boolean onboardingUpdated) {
        this.onboardingUpdated = onboardingUpdated;
    }

    public boolean isAppUpdateRequired() {
        return appUpdateRequired;
    }

    public void setAppUpdateRequired(boolean appUpdateRequired) {
        this.appUpdateRequired = appUpdateRequired;
    }

    public String getAppUpdateDescription() {
        return appUpdateDescription;
    }

    public void setAppUpdateDescription(String appUpdateDescription) {
        this.appUpdateDescription = appUpdateDescription;
    }
}
