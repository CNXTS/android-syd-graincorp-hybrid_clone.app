package com.webling.graincorp.data.api.model.response.user;

import com.google.gson.annotations.SerializedName;

public class UserSettings {
    @SerializedName("GrowerSettingsSet")
    private GrowerSettings growerSettings = new GrowerSettings();
    @SerializedName("BuyerSettingsSet")
    private BuyerSettings buyerSettings = new BuyerSettings();

    public UserSettings(String defaultNgr, String accountNo) {
        growerSettings.setDefaultNgr(defaultNgr);
        buyerSettings.setDefaultAcc(accountNo);
    }

    public GrowerSettings getGrowerSettings() {
        return growerSettings;
    }

    public void setGrowerSettings(GrowerSettings growerSettings) {
        this.growerSettings = growerSettings;
    }

    public BuyerSettings getBuyerSettings() {
        return buyerSettings;
    }

    public void setBuyerSettings(BuyerSettings buyerSettings) {
        this.buyerSettings = buyerSettings;
    }

    public class GrowerSettings {

        @SerializedName("DefaultNgr")
        private String defaultNgr = "";

        public GrowerSettings() {
        }

        public GrowerSettings(String defaultNgr) {
            this.defaultNgr = defaultNgr;
        }

        public String getDefaultNgr() {
            return defaultNgr;
        }

        public void setDefaultNgr(String defaultNgr) {
            this.defaultNgr = defaultNgr;
        }
    }

    public class BuyerSettings {

        @SerializedName("DefaultAcc")
        private String defaultAcc = "";

        public BuyerSettings() {
        }

        public BuyerSettings(String defaultAcc) {
            this.defaultAcc = defaultAcc;
        }

        public String getDefaultAcc() {
            return defaultAcc;
        }

        public void setDefaultAcc(String defaultAcc) {
            this.defaultAcc = defaultAcc;
        }
    }
}