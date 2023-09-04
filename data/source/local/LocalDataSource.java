package com.webling.graincorp.data.source.local;

import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.model.DeviceSettings;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.MenuParentGroup;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.PushNotification;
import com.webling.graincorp.model.SearchFilter;
import com.webling.graincorp.model.UserInfo;

import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;

public interface LocalDataSource {

    Observable<List<PushNotification>> getAllNotificationsAsObservable();

    List<PushNotification> getAllNotifications();

    GlobalSettings getGlobalSettings();

    DeviceId getDeviceId();

    void setDeviceIdIfNotExists(UUID uuid);

    void saveGlobalSettings(GlobalSettings globalSettings);

    void saveNotification(PushNotification notification);

    void saveNotifications(List<PushNotification> notifications);

    void deleteNotification(PushNotification notification);

    void deleteAllNotifications();

    List<NotificationSettingsGroup> getPushNotificationSettings(String deviceId);

    Observable<List<NotificationSettingsGroup>> getPushNotificationSettingsAsObservable(String deviceId);

    void savePushNotificationSettings(List<NotificationSettingsGroup> notifications);

    void saveMenu(List<MenuParentGroup> menuParentGroups);

    List<MenuParentGroup> getMenu(String userType, String accountNo);

    void saveUserInfo(UserInfo userInfo);

    UserInfo getUserInfo();

    void setUserType(@UserInfo.AccountGroupTypeDef String userType);

    void clearUserInfo();

    void saveSearchFilters(List<SearchFilter> searchFilters);

    List<SearchFilter> getSearchFilters();

    void saveBids(List<Bid> bids);

    void deleteBidsAndSearchFilters();

    List<Bid> getBids();

    DeviceSettings getDeviceSettings();

    void saveDeviceSettings(DeviceSettings deviceSettings);

    void setCsrfToken(CSRFToken csrfToken);

    CSRFToken getCsrfToken();
}
