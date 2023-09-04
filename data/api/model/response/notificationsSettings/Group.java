
package com.webling.graincorp.data.api.model.response.notificationsSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.webling.graincorp.data.api.model.response.ResultsListWrapper;

public class Group {

    @SerializedName("UserDeviceID")
    @Expose
    private String userDeviceId;
    @SerializedName("PnGroupId")
    @Expose
    private String pnGroupId;
    @SerializedName("MasterPnId")
    @Expose
    private String masterPnId;
    @SerializedName("PnGroupName")
    @Expose
    private String pnGroupName;
    @SerializedName("SettingsGroupItemSet")
    @Expose
    private ResultsListWrapper<Item> settingsGroupItemSet;

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

    public String getPnGroupName() {
        return pnGroupName;
    }

    public void setPnGroupName(String pnGroupName) {
        this.pnGroupName = pnGroupName;
    }

    public ResultsListWrapper<Item> getSettingsGroupItemSet() {
        return settingsGroupItemSet;
    }

    public void setSettingsGroupItemSet(ResultsListWrapper<Item> settingsGroupItemSet) {
        this.settingsGroupItemSet = settingsGroupItemSet;
    }

    public String getMasterPnId() {
        return masterPnId;
    }

    public void setMasterPnId(String masterPnId) {
        this.masterPnId = masterPnId;
    }
}
