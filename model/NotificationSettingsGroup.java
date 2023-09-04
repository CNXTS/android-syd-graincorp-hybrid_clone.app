package com.webling.graincorp.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.util.List;

@Table(database = GrainCorpDatabase.class)
public class NotificationSettingsGroup extends BaseModel {

    @PrimaryKey
    @SerializedName("PnGroupId")
    @Expose
    private String pnGroupId;

    @Column
    @SerializedName("MasterPnId")
    @Expose
    private String masterPnId;

    @Column
    @SerializedName("UserDeviceID")
    @Expose
    private String userDeviceId;

    @Column
    @SerializedName("PnGroupName")
    @Expose
    private String pnGroupName;

    public List<NotificationSettingsItem> notificationSettingsItems;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "notificationSettingsItems", efficientMethods = false)
    public List<NotificationSettingsItem> getNotificationSettingsItemsFromTable() {
        if (notificationSettingsItems == null || notificationSettingsItems.isEmpty()) {
            notificationSettingsItems = new Select().from(NotificationSettingsItem.class)
                    .where(NotificationSettingsItem_Table.pnGroupId.eq(pnGroupId))
                    .queryList();
        }

        return notificationSettingsItems;
    }

    public void associateGroupItems() {
        for (NotificationSettingsItem item : notificationSettingsItems) {
            item.setPnGroupId(getPnGroupId());
        }
    }

    @Override
    public boolean save() {
        associateGroupItems();
        return super.save();
    }

    @Override
    public boolean save(@NonNull DatabaseWrapper databaseWrapper) {
        associateGroupItems();
        return super.save(databaseWrapper);
    }

    public NotificationSettingsGroup() {
    }

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

    public List<NotificationSettingsItem> getNotificationSettingsItems() {
        return notificationSettingsItems == null ? getNotificationSettingsItemsFromTable() : notificationSettingsItems;
    }

    public void setNotificationSettingsItems(List<NotificationSettingsItem> notificationSettingsItems) {
        this.notificationSettingsItems = notificationSettingsItems;
    }

    public String getMasterPnId() {
        return masterPnId;
    }

    public void setMasterPnId(String masterPnId) {
        this.masterPnId = masterPnId;
    }

    public boolean isMasterSwitchGroup() {
        return pnGroupId.equals(masterPnId);
    }
}
