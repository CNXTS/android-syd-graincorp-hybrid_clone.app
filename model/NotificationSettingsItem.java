package com.webling.graincorp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

@Table(database = GrainCorpDatabase.class)
public class NotificationSettingsItem extends BaseModel {

    @Column
    @SerializedName("UserDeviceID")
    @Expose
    private String userDeviceID;

    @PrimaryKey
    @SerializedName("PnGroupId")
    @Expose
    private String pnGroupId;

    @PrimaryKey
    @SerializedName("PnId")
    @Expose
    private String pnId;

    @Column
    @SerializedName("PnTitle")
    @Expose
    private String pnTitle;

    @Column
    @SerializedName("PnConfigUrl")
    @Expose
    private String pnConfigUrl;

    @Column
    @SerializedName("PnDescription")
    @Expose
    private String pnDescription;

    @Column
    @SerializedName("PnPhoneNo")
    @Expose
    private String pnPhoneNo;

    @Column
    @SerializedName("Active")
    @Expose
    private boolean active;

    @Column
    private String masterPnId;


    public NotificationSettingsItem() {
    }

    public String getUserDeviceID() {
        return userDeviceID;
    }

    public void setUserDeviceID(String userDeviceID) {
        this.userDeviceID = userDeviceID;
    }

    public String getPnGroupId() {
        return pnGroupId;
    }

    public void setPnGroupId(String pnGroupId) {
        this.pnGroupId = pnGroupId;
    }

    public String getPnId() {
        return pnId;
    }

    public void setPnId(String pnId) {
        this.pnId = pnId;
    }

    public String getPnTitle() {
        return pnTitle;
    }

    public void setPnTitle(String pnTitle) {
        this.pnTitle = pnTitle;
    }

    public String getPnConfigUrl() {
        return pnConfigUrl;
    }

    public void setPnConfigUrl(String pnConfigUrl) {
        this.pnConfigUrl = pnConfigUrl;
    }

    public String getPnDescription() {
        return pnDescription;
    }

    public void setPnDescription(String pnDescription) {
        this.pnDescription = pnDescription;
    }

    public String getPnPhoneNo() {
        return pnPhoneNo;
    }

    public void setPnPhoneNo(String pnPhoneNo) {
        this.pnPhoneNo = pnPhoneNo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMasterPnId() {
        return masterPnId;
    }

    public void setMasterPnId(String masterPnId) {
        this.masterPnId = masterPnId;
    }

    public boolean isMasterSwitchItem() {
        return pnId.equals(masterPnId);
    }
}
