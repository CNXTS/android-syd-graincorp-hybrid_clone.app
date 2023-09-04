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
public class MenuParentGroup extends BaseModel {

    @PrimaryKey
    @SerializedName("ParentId")
    @Expose
    private String parentId;

    @Column
    @SerializedName("UserType")
    @Expose
    private String userType;

    @Column
    @SerializedName("AccountNo")
    @Expose
    private String accountNo;

    @Column
    @SerializedName("Title")
    @Expose
    private String title;

    public List<MenuChildItem> menuChildItems;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "menuChildItems", efficientMethods = false)
    public List<MenuChildItem> getMenuChildItemsFromTable() {
        if (menuChildItems == null || menuChildItems.isEmpty()) {
            menuChildItems = new Select().from(MenuChildItem.class)
                    .where(MenuChildItem_Table.parentId.eq(parentId))
                    .queryList();
        }

        return menuChildItems;
    }

    public void associateGroupItems() {
        for (MenuChildItem item : menuChildItems) {
            item.setParentId(getParentId());
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

    public MenuParentGroup() {
    }

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

    public List<MenuChildItem> getMenuChildItems() {
        return menuChildItems == null ? getMenuChildItemsFromTable() : menuChildItems;
    }

    public void setMenuChildItems(List<MenuChildItem> menuChildItems) {
        this.menuChildItems = menuChildItems;
    }
}
