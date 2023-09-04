package com.webling.graincorp.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.util.Objects;


@Table(database = GrainCorpDatabase.class)
public class GlobalSettings extends BaseModel {

    @PrimaryKey
    @Column(defaultValue = "1")
    private Integer id = 1;

    @Column
    @SerializedName("PnAge")
    private int pushNotificationAgeInDays;

    @Column
    @SerializedName("OfferDefaultValidity")
    private int offerDefaultValidity;

    @Column
    @SerializedName("GtDomain")
    private String gtDomain;

    @Column
    @SerializedName("AdflDomain")
    private String adflDomain;

    @Column
    @SerializedName("UserDeviceID")
    private String deviceId;

    @Column
    @SerializedName("CCDomain")
    private String ccDomain;

    @Column
    @SerializedName("InactSeconds")
    private int inactiveAgeInSeconds;

    @Column
    @SerializedName("CCWFURL")
    private String CCWFURL;

    @Column
    @SerializedName("ForgotPwdUrl")
    private String forgotPwdUrl;

    @Column
    @SerializedName("LoginUrl")
    private String loginUrl;

    @Column
    @SerializedName("TermsAndCondUrl")
    private String termsAndConditionsUrl;

    @Column
    @SerializedName("AllBidUrl")
    private String allBidUrl;

    @Column
    @SerializedName("GrowerDelsummUrl")
    private String growerDeliverySummaryUrl;

    @Column
    @SerializedName("SiteUrl")
    private String siteUrl;

    @Column
    @SerializedName("OtherSettingsUrl")
    private String otherSettingsUrl;

    @Column
    @SerializedName("MaintURL")
    private String maintenanceUrl;

    @Column
    @SerializedName("ExtGwCookie")
    private String sessionCookieName;

    @Column
    @SerializedName("ShowPinSetupAfterOnboard")
    private boolean showPinSetupAfterOnboarding;

    @Column
    @SerializedName("MaxPinFail")
    private int maxPinFail;

    @Column
    @SerializedName("LogOffUrl")
    private String logOffUrl;

    public GlobalSettings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPushNotificationAgeInDays() {
        return pushNotificationAgeInDays;
    }

    public void setPushNotificationAgeInDays(int pushNotificationAgeInDays) {
        this.pushNotificationAgeInDays = pushNotificationAgeInDays;
    }

    public int getOfferDefaultValidity() {
        return offerDefaultValidity;
    }

    public void setOfferDefaultValidity(int offerDefaultValidity) {
        this.offerDefaultValidity = offerDefaultValidity;
    }

    public String getGtDomain() {
        return gtDomain;
    }

    public void setGtDomain(String gtDomain) {
        this.gtDomain = gtDomain;
    }

    public String getAdflDomain() {
        return adflDomain;
    }

    public void setAdflDomain(String adflDomain) {
        this.adflDomain = adflDomain;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCcDomain() {
        return ccDomain;
    }

    public void setCcDomain(String ccDomain) {
        this.ccDomain = ccDomain;
    }

    public int getInactiveAgeInSeconds() {
        return inactiveAgeInSeconds;
    }

    public void setInactiveAgeInSeconds(int inactiveAgeInSeconds) {
        this.inactiveAgeInSeconds = inactiveAgeInSeconds;
    }

    public String getCCWFURL() {
        return CCWFURL;
    }

    public void setCCWFURL(String CCWFURL) {
        this.CCWFURL = CCWFURL;
    }

    public String getForgotPwdUrl() {
        return forgotPwdUrl;
    }

    public void setForgotPwdUrl(String forgotPwdUrl) {
        this.forgotPwdUrl = forgotPwdUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getTermsAndConditionsUrl() {
        return termsAndConditionsUrl;
    }

    public void setTermsAndConditionsUrl(String termsAndConditionsUrl) {
        this.termsAndConditionsUrl = termsAndConditionsUrl;
    }

    public String getAllBidUrl() {
        return allBidUrl;
    }

    public void setAllBidUrl(String allBidUrl) {
        this.allBidUrl = allBidUrl;
    }

    public String getGrowerDeliverySummaryUrl() {
        return growerDeliverySummaryUrl;
    }

    public void setGrowerDeliverySummaryUrl(String growerDeliverySummaryUrl) {
        this.growerDeliverySummaryUrl = growerDeliverySummaryUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getOtherSettingsUrl() {
        return otherSettingsUrl;
    }

    public void setOtherSettingsUrl(String otherSettingsUrl) {
        this.otherSettingsUrl = otherSettingsUrl;
    }

    public String getMaintenanceUrl() {
        return maintenanceUrl;
    }

    public void setMaintenanceUrl(String maintenanceUrl) {
        this.maintenanceUrl = maintenanceUrl;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public boolean isShowPinSetupAfterOnboarding() {
        return showPinSetupAfterOnboarding;
    }

    public void setShowPinSetupAfterOnboarding(boolean showPinSetupAfterOnboarding) {
        this.showPinSetupAfterOnboarding = showPinSetupAfterOnboarding;
    }

    public int getMaxPinFail() {
        return maxPinFail;
    }

    public void setMaxPinFail(int maxPinFail) {
        this.maxPinFail = maxPinFail;
    }

    public String getLogOffUrl() {
        return logOffUrl;
    }

    public void setLogOffUrl(String logOffUrl) {
        this.logOffUrl = logOffUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalSettings)) return false;
        GlobalSettings that = (GlobalSettings) o;
        return getPushNotificationAgeInDays() == that.getPushNotificationAgeInDays() &&
                getOfferDefaultValidity() == that.getOfferDefaultValidity() &&
                getInactiveAgeInSeconds() == that.getInactiveAgeInSeconds() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getGtDomain(), that.getGtDomain()) &&
                Objects.equals(getAdflDomain(), that.getAdflDomain()) &&
                Objects.equals(getDeviceId(), that.getDeviceId()) &&
                Objects.equals(getCcDomain(), that.getCcDomain()) &&
                Objects.equals(getCCWFURL(), that.getCCWFURL()) &&
                Objects.equals(getForgotPwdUrl(), that.getForgotPwdUrl()) &&
                Objects.equals(getLoginUrl(), that.getLoginUrl()) &&
                Objects.equals(getTermsAndConditionsUrl(), that.getTermsAndConditionsUrl()) &&
                Objects.equals(getAllBidUrl(), that.getAllBidUrl()) &&
                Objects.equals(getGrowerDeliverySummaryUrl(), that.getGrowerDeliverySummaryUrl()) &&
                Objects.equals(getSiteUrl(), that.getSiteUrl()) &&
                Objects.equals(getOtherSettingsUrl(), that.getOtherSettingsUrl()) &&
                Objects.equals(getMaintenanceUrl(), that.getMaintenanceUrl()) &&
                Objects.equals(getSessionCookieName(), that.getSessionCookieName()) &&
                isShowPinSetupAfterOnboarding() == that.isShowPinSetupAfterOnboarding() &&
                getMaxPinFail() == that.getMaxPinFail() &&
                Objects.equals(getLogOffUrl(), that.getLogOffUrl());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPushNotificationAgeInDays(), getOfferDefaultValidity(), getGtDomain(),
                getAdflDomain(), getDeviceId(), getCcDomain(), getInactiveAgeInSeconds(), getCCWFURL(),
                getForgotPwdUrl(), getLoginUrl(), getTermsAndConditionsUrl(), getAllBidUrl(),
                getGrowerDeliverySummaryUrl(), getSiteUrl(), getOtherSettingsUrl(),
                getMaintenanceUrl(), getSessionCookieName(), isShowPinSetupAfterOnboarding(), getMaxPinFail(), getLogOffUrl());
    }
}
