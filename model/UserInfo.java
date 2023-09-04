package com.webling.graincorp.model;

import androidx.annotation.StringDef;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Table(database = GrainCorpDatabase.class,
        uniqueColumnGroups = {@UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.REPLACE)})
public class UserInfo extends BaseModel {
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    private boolean isGrower;
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    private boolean isBuyer;
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    private boolean isFreightProvider;
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    @Column(defaultValue = "")
    private String accountNo = "";
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    @Column(defaultValue = "")
    private String ngrNo = "";
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    @Column(defaultValue = AccountGroupTypeDef.TYPE_EMPTY)
    private String accountGroup = AccountGroupTypeDef.TYPE_EMPTY;
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    private boolean isTermsAccepted;

    @StringDef({AccountGroupTypeDef.TYPE_GROWER, AccountGroupTypeDef.TYPE_BUYER, AccountGroupTypeDef.TYPE_EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AccountGroupTypeDef {
        String TYPE_GROWER = "ZNGR";
        String TYPE_BUYER = "ZSHB";
        String TYPE_EMPTY = "";
    }

    public UserInfo() {
    }

    public UserInfo(boolean isGrower, boolean isBuyer, boolean isFreightProvider, String ngrNo, String accountNo, @AccountGroupTypeDef String accountGroup, boolean isTermsAccepted) {
        this.isGrower = isGrower;
        this.isBuyer = isBuyer;
        this.isFreightProvider = isFreightProvider;
        this.ngrNo = ngrNo;
        this.accountNo = accountNo;
        this.accountGroup = accountGroup;
        this.isTermsAccepted = isTermsAccepted;

    }

    public
    @AccountGroupTypeDef
    String getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(@AccountGroupTypeDef String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public String getRelevantAccountNumber() {
        @AccountGroupTypeDef String currentAccountGroup = getAccountGroup();
        if (currentAccountGroup.equals(AccountGroupTypeDef.TYPE_GROWER)) return getNgrNo();
        if (currentAccountGroup.equals(AccountGroupTypeDef.TYPE_BUYER)) return getAccountNo();

        return "";
    }

    public
    @AccountGroupTypeDef
    String getSwitchableAccountGroup() {

        if (!hasBuyerAndGrowerRoles()) return AccountGroupTypeDef.TYPE_EMPTY;

        @AccountGroupTypeDef String currentAccountGroup = getAccountGroup();
        if (currentAccountGroup.equals(AccountGroupTypeDef.TYPE_GROWER) && isBuyer()) {
            return AccountGroupTypeDef.TYPE_BUYER;
        } else if (currentAccountGroup.equals(AccountGroupTypeDef.TYPE_BUYER) && isGrower()) {
            return AccountGroupTypeDef.TYPE_GROWER;
        } else {
            return AccountGroupTypeDef.TYPE_EMPTY;
        }

    }

    public boolean hasBuyerAndGrowerRoles() {
        return hasRoles(isGrower, isBuyer) >= 2;
    }

    private int hasRoles(boolean... roles) {
        int count = 0;
        for (boolean role : roles) {
            count += (role ? 1 : 0);
        }
        return count;
    }

    //TODO error prone with setgrower and setaccountno.
    public boolean isGrower() {
        return isGrower;
    }

    public void setGrower(boolean grower) {
        isGrower = grower;
    }

    public boolean isBuyer() {
        return isBuyer;
    }

    public void setBuyer(boolean buyer) {
        isBuyer = buyer;
    }

    public boolean isFreightProvider() {
        return isFreightProvider;
    }

    public boolean isFreightProviderOnly() {
        return isFreightProvider && !(isGrower() || isBuyer());
    }

    public void setFreightProvider(boolean freightProvider) {
        isFreightProvider = freightProvider;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getNgrNo() {
        return ngrNo;
    }

    public void setNgrNo(String ngrNo) {
        this.ngrNo = ngrNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public boolean isTermsAccepted() {
        return isTermsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.isTermsAccepted = termsAccepted;
    }
}
