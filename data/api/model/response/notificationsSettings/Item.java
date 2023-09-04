
package com.webling.graincorp.data.api.model.response.notificationsSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("UserDeviceID")
    @Expose
    private String userDeviceId;
    @SerializedName("PnGroupId")
    @Expose
    private String pnGroupId;
    @SerializedName("PnId")
    @Expose
    private String pnId;
    @SerializedName("PnTitle")
    @Expose
    private String pnTitle;
    @SerializedName("PnConfigUrl")
    @Expose
    private String pnConfigUrl;
    @SerializedName("PnDescription")
    @Expose
    private String pnDescription;
    @SerializedName("PnPhoneNo")
    @Expose
    private String pnPhoneNo;
    @SerializedName("Active")
    @Expose
    private boolean active;

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
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

}
