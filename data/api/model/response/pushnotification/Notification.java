package com.webling.graincorp.data.api.model.response.pushnotification;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("PnId")
    private String pnId;

    @SerializedName("UserDeviceID")
    private String userDeviceId;

    @SerializedName("TransactionTimestamp")
    private String transactionTimeStamp;

    @SerializedName("PnMsgTitle")
    private String title;

    @SerializedName("PnTimezoneText")
    private String timeZoneText;
    @SerializedName("PnMsgBody")
    private String body;

    @SerializedName("PnActionUrl")
    private String actionUrl;

    public Notification() {
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

    public String getTransactionTimeStamp() {
        return transactionTimeStamp;
    }

    public void setTransactionTimeStamp(String transactionTimeStamp) {
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
