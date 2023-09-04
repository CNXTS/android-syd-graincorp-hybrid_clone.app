package com.webling.graincorp.data.api.model.response.user;

import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("GrowerFlag")
    private boolean growerFlag;
    @SerializedName("BuyerFlag")
    private boolean buyerFlag;
    @SerializedName("FreightpFlag")
    private boolean freightPFlag;

    public UserDetails() {
    }

    public UserDetails(boolean growerFlag, boolean buyerFlag, boolean freightPFlag) {
        this.growerFlag = growerFlag;
        this.buyerFlag = buyerFlag;
        this.freightPFlag = freightPFlag;
    }

    public boolean isGrowerFlag() {
        return growerFlag;
    }

    public void setGrowerFlag(boolean growerFlag) {
        this.growerFlag = growerFlag;
    }

    public boolean isBuyerFlag() {
        return buyerFlag;
    }

    public void setBuyerFlag(boolean buyerFlag) {
        this.buyerFlag = buyerFlag;
    }

    public boolean isFreightPFlag() {
        return freightPFlag;
    }

    public void setFreightPFlag(boolean freightPFlag) {
        this.freightPFlag = freightPFlag;
    }
}
