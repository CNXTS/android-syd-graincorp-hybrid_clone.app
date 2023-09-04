package com.webling.graincorp.data.api.model.response.user;

import com.google.gson.annotations.SerializedName;

public class UserAccount {
    @SerializedName("AccountNo")
    private String accountNo = "";

    public UserAccount() {
    }

    public UserAccount(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
