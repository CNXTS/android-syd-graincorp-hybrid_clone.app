package com.webling.graincorp.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.converter.DateConverter;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.util.Date;
import java.util.UUID;

//TODO use better DBFLOW DSL for conflict replace and all columns etc...
@Table(database = GrainCorpDatabase.class)
public class PushNotification extends BaseModel {

    @PrimaryKey
    private UUID id = UUID.randomUUID();

    @Column
    @SerializedName("PnId")
    private String pnId;

    @Column
    @SerializedName("UserDeviceID")
    private String userDeviceId;

    @Column(typeConverter = DateConverter.class)
    @SerializedName("TransactionTimestamp")
    private Date transactionTimeStamp;

    @Column
    @SerializedName("PnMsgTitle")
    private String title;

    @Column(defaultValue = "")
    @SerializedName("PnTimezoneText")
    private String timeZoneText = "";

    @Column
    @SerializedName("PnMsgBody")
    private String body;

    @Column
    @SerializedName("PnActionUrl")
    private String actionUrl;

    public PushNotification() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPnId() {
        return pnId;
    }

    public void setPnId(String pnId) {
        this.pnId = pnId;
    }

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public Date getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionTimeStamp(Date transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getTimeZoneText() {
        return timeZoneText;
    }

    public void setTimeZoneText(String timeZoneText) {
        this.timeZoneText = timeZoneText;
    }
}
