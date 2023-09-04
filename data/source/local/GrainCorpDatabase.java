package com.webling.graincorp.data.source.local;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.rx2.language.RXSQLite;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.Bid_Table;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.model.DeviceSettings;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.MenuChildItem;
import com.webling.graincorp.model.MenuParentGroup;
import com.webling.graincorp.model.MenuParentGroup_Table;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.NotificationSettingsItem;
import com.webling.graincorp.model.PushNotification;
import com.webling.graincorp.model.PushNotification_Table;
import com.webling.graincorp.model.SearchFilter;
import com.webling.graincorp.model.SearchFilter_Table;
import com.webling.graincorp.model.UserInfo;
import com.webling.graincorp.model.UserInfo_Table;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.GlobalEvent;

import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;

import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_EMPTY;

@Database(version = GrainCorpDatabase.VERSION)
public class GrainCorpDatabase implements LocalDataSource {
    public static final String NAME = "GrainCorpDatabase";

    static final int VERSION = 3;

    @NonNull
    private DatabaseDefinition database;
    private RxBus<GlobalEvent> globalEventRxBus;
    private CSRFToken csrfToken;

    @Migration(version = 2, database = GrainCorpDatabase.class)
    public static class Migration_1_2 extends BaseMigration {
        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            database.beginTransaction();

            //Create globalSettings table with the new schema(removing pnCount column) and name it as backup
            database.execSQL(
                    "CREATE TABLE `GlobalSettings_backup`" +
                            "(`id` INTEGER DEFAULT 1 PRIMARY KEY," +
                            "`pushNotificationAgeInDays` INTEGER," +
                            "`offerDefaultValidity` INTEGER," +
                            "`gtDomain` TEXT," +
                            "`adflDomain` TEXT," +
                            "`deviceId` TEXT, " +
                            "`ccDomain` TEXT," +
                            "`inactiveAgeInSeconds` INTEGER," +
                            "`CCWFURL` TEXT," +
                            "`forgotPwdUrl` TEXT," +
                            "`loginUrl` TEXT," +
                            "`termsAndConditionsUrl` TEXT," +
                            "`allBidUrl` TEXT," +
                            "`growerDeliverySummaryUrl` TEXT," +
                            "`siteUrl` TEXT," +
                            "`otherSettingsUrl` TEXT," +
                            "`maintenanceUrl` TEXT, " +
                            "`sessionCookieName` TEXT);");

            //Migrate data from existing globalSettings table to the new table with the new schema
            database.execSQL("INSERT INTO `GlobalSettings_backup` SELECT `id`," +
                    "`pushNotificationAgeInDays`," +
                    "`offerDefaultValidity`," +
                    "`gtDomain`," +
                    "`adflDomain`," +
                    "`deviceId`, " +
                    "`ccDomain`," +
                    "`inactiveAgeInSeconds`," +
                    "`CCWFURL`," +
                    "`forgotPwdUrl`," +
                    "`loginUrl`," +
                    "`termsAndConditionsUrl`," +
                    "`allBidUrl`," +
                    "`growerDeliverySummaryUrl`," +
                    "`siteUrl`," +
                    "`otherSettingsUrl`," +
                    "`maintenanceUrl`, " +
                    "`sessionCookieName` FROM `GlobalSettings`;");


            //Delete existing globalSettings table
            database.execSQL("DROP TABLE `GlobalSettings`;");
            //Rename the new globalSettings table with the new schema previously named as globalSettings_backup to the normal table name
            database.execSQL("ALTER TABLE `GlobalSettings_backup` RENAME TO `GlobalSettings`;");

            //Add new column
            database.execSQL("ALTER TABLE `GlobalSettings` ADD COLUMN showPinSetupAfterOnboarding INTEGER;");
            //Add new column
            database.execSQL("ALTER TABLE `GlobalSettings` ADD COLUMN maxPinFail INTEGER;");

            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    @Migration(version = 3, database = GrainCorpDatabase.class)
    public static class Migration_2_3 extends BaseMigration {
        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            database.beginTransaction();
            String query = "ALTER TABLE GlobalSettings ADD COLUMN logOffUrl VARCHAR(255);";
            database.execSQL(query);
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    public GrainCorpDatabase(@NonNull DatabaseDefinition databaseDefinition,
                             RxBus<GlobalEvent> globalEventRxBus,
                             CSRFToken csrfToken) {
        database = databaseDefinition;
        this.globalEventRxBus = globalEventRxBus;
        this.csrfToken = csrfToken;
    }

    @Override
    public Observable<List<PushNotification>> getAllNotificationsAsObservable() {
        return RXSQLite.rx(
                SQLite.select()
                        .from(PushNotification.class)
                        .orderBy(PushNotification_Table.transactionTimeStamp, false)
        ).queryList().toObservable();
    }

    @Override
    public List<PushNotification> getAllNotifications() {
        return SQLite.select()
                .from(PushNotification.class)
                .orderBy(PushNotification_Table.transactionTimeStamp, false)
                .queryList();
    }

    @Override
    public GlobalSettings getGlobalSettings() {
        return SQLite.select()
                .from(GlobalSettings.class)
                .where()
                .querySingle();
    }

    @Override
    public DeviceId getDeviceId() {
        return SQLite.select()
                .from(DeviceId.class)
                .where().querySingle();
    }

    @Override
    public void setDeviceIdIfNotExists(UUID uuid) {
        database.executeTransaction(databaseWrapper -> {
            if (SQLite.select().from(DeviceId.class).querySingle(databaseWrapper) == null) {
                new DeviceId(uuid).save();
            }
        });
    }

    @Override
    public void saveGlobalSettings(GlobalSettings globalSettings) {
        globalSettings.save();
    }

    @Override
    public void saveNotification(PushNotification notification) {
        notification.save();
    }

    @WorkerThread
    @Override
    public void saveNotifications(List<PushNotification> notifications) {
        save(notifications);
    }

    @Override
    public void deleteNotification(PushNotification notification) {
        notification.delete();
    }

    @Override
    public void deleteAllNotifications() {
        SQLite.delete().from(PushNotification.class).executeUpdateDelete();
    }

    @Override
    public List<NotificationSettingsGroup> getPushNotificationSettings(String deviceId) {
        return SQLite.select().from(NotificationSettingsGroup.class).queryList();
    }

    @Override
    public Observable<List<NotificationSettingsGroup>> getPushNotificationSettingsAsObservable(String deviceId) {
        return RXSQLite.rx(
                SQLite.select()
                        .from(NotificationSettingsGroup.class)
                        .where()
        ).queryList().toObservable();
    }

    @Override
    public void savePushNotificationSettings(List<NotificationSettingsGroup> notifications) {
        SQLite.delete().from(NotificationSettingsGroup.class).executeUpdateDelete();
        SQLite.delete().from(NotificationSettingsItem.class).executeUpdateDelete();
        save(notifications);
    }

    @Override
    public void saveMenu(List<MenuParentGroup> menuParentGroups) {
        database.executeTransaction(databaseWrapper -> {
            SQLite.delete().from(MenuParentGroup.class).execute();
            SQLite.delete().from(MenuChildItem.class).execute();
            for (MenuParentGroup menuParentGroup : menuParentGroups) {
                menuParentGroup.save();
            }
        });
    }

    @Override
    public List<MenuParentGroup> getMenu(String userType, String accountNo) {
        return SQLite.select().from(MenuParentGroup.class)
                .where(MenuParentGroup_Table.userType.eq(userType))
                .or(MenuParentGroup_Table.userType.eq(TYPE_EMPTY))
                .orderBy(MenuParentGroup_Table.parentId, true)
                .queryList();

    }

    @Override
    public void saveUserInfo(UserInfo user) {
        SQLite.delete().from(UserInfo.class).executeUpdateDelete();
        user.save();
    }

    @Override
    public UserInfo getUserInfo() {
        return SQLite.select()
                .from(UserInfo.class)
                .where().querySingle();
    }

    @Override
    public void setUserType(@UserInfo.AccountGroupTypeDef String userType) {
        SQLite.update(UserInfo.class).set(UserInfo_Table.accountGroup.eq(userType)).execute();
    }

    @Override
    public void clearUserInfo() {
        SQLite.delete().from(UserInfo.class).executeUpdateDelete();
    }

    @Override
    public void saveSearchFilters(List<SearchFilter> searchFilters) {
        database.executeTransaction(databaseWrapper -> {
            SQLite.delete().from(SearchFilter.class).execute();
            for (SearchFilter searchFilter : searchFilters) {
                searchFilter.save();
            }
        });
    }

    @Override
    public List<SearchFilter> getSearchFilters() {
        return SQLite.select()
                .from(SearchFilter.class)
                .orderBy(SearchFilter_Table.timestamp, true)
                .queryList();
    }

    @Override
    public void saveBids(List<Bid> bids) {
        SQLite.delete().from(Bid.class).executeUpdateDelete();
        save(bids);
    }

    @Override
    public void deleteBidsAndSearchFilters() {
        SQLite.delete().from(Bid.class).executeUpdateDelete();
        SQLite.delete().from(SearchFilter.class).executeUpdateDelete();
    }

    @Override
    public List<Bid> getBids() {
        return SQLite.select()
                .from(Bid.class)
                .orderBy(Bid_Table.timestamp, true)
                .queryList();
    }

    @Override
    public DeviceSettings getDeviceSettings() {
        return SQLite.select()
                .from(DeviceSettings.class)
                .where().querySingle();
    }

    @Override
    public void saveDeviceSettings(DeviceSettings deviceSettings) {
        deviceSettings.save();
    }

    @Override
    public void setCsrfToken(CSRFToken csrfToken) {
        this.csrfToken = csrfToken;
    }

    @Override
    public CSRFToken getCsrfToken() {
        return csrfToken;
    }

    private void save(List<? extends BaseModel> models) {
        //TODO use better DBFlow DSL
        //TODO Use FastStoreModelTransaction
        ProcessModelTransaction<BaseModel> processModelTransaction =
                new ProcessModelTransaction.Builder<>(
                        (ProcessModelTransaction.ProcessModel<BaseModel>)
                                (items, wrapper) -> items.save())
                        .processListener(
                                (current, total, modifiedModel) -> {
                                })
                        .addAll(models).build();

        processModelTransaction.execute(database.getWritableDatabase());

    }
}