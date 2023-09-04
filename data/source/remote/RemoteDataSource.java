package com.webling.graincorp.data.source.remote;

import androidx.annotation.Nullable;

import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceSettings;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.MenuParentGroup;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.OnboardingItem;
import com.webling.graincorp.model.PushNotification;
import com.webling.graincorp.model.SearchFilter;
import com.webling.graincorp.model.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Response;

public interface RemoteDataSource {
    Observable<GlobalSettings> getGlobalSettings(String email, String platform, String appVersion, String deviceId, boolean secure);

    Observable<List<NotificationSettingsGroup>> getPushNotificationSettings(String deviceId, String email);

    Observable<List<NotificationSettingsGroup>> setPushNotificationSettings(Map<String, Boolean> notifications, String deviceId, String email);

    Observable<List<MenuParentGroup>> getMenu(String deviceId, String userType, String accountNo, boolean isLoggedIn);

    Observable<List<PushNotification>> getNotifications(String deviceId, String email);

    Observable<Response<Object>> setNotificationCount(String emailAddress, int pushNotificaitonCount, String deviceId);

    Observable<UserInfo> getUserInfo();

    Observable<List<OnboardingItem>> getOnboardingItems(String email, String deviceId);

    Observable<OnboardingItem> seenOnboardingItem(String email, String deviceId, int item);

    Observable<List<Bid>> getBids(List<SearchFilter> searchFilters);

    Observable<Boolean> getTermsStatus();

    Observable<DeviceSettings> getDeviceSettings(String userDeviceId, String email, boolean secure);

    Observable<DeviceSettings> deactivatePin(String userDeviceId, String email, boolean secure, CSRFToken csrfToken);

    Observable<DeviceSettings> activatePin(String userDeviceId, String pin, String email, boolean secure, CSRFToken csrfToken);

    Observable<DeviceSettings> checkPin(String userDeviceId, @Nullable String pin, String email, boolean secure);
}
