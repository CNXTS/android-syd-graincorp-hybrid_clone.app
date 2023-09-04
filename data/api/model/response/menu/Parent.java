
package com.webling.graincorp.data.api.model.response.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.webling.graincorp.data.api.model.response.ResultsListWrapper;

@SuppressWarnings("unused")
public class Parent {

    @SerializedName("UserType")
    @Expose
    private String userType;
    @SerializedName("AccountNo")
    @Expose
    private String accountNo;
    @SerializedName("ParentId")
    @Expose
    private String parentId;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("MenuChildSet")
    @Expose
    private ResultsListWrapper<Child> menuChildSet;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ResultsListWrapper<Child> getMenuChildSet() {
        return menuChildSet;
    }

    public void setMenuChildSet(ResultsListWrapper<Child> menuChildSet) {
        this.menuChildSet = menuChildSet;
    }
}
