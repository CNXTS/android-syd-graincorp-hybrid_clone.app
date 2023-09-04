package com.webling.graincorp.data.source.remote;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.webling.graincorp.BuildConfig;
import com.webling.graincorp.constants.ApiConstants;
import com.webling.graincorp.data.api.ApiService;
import com.webling.graincorp.data.api.model.response.user.UserAccount;
import com.webling.graincorp.data.api.model.response.user.UserDetails;
import com.webling.graincorp.data.api.model.response.user.UserSettings;
import com.webling.graincorp.data.exception.SessionExpiredException;
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
import com.webling.graincorp.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef;
import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_BUYER;
import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_EMPTY;
import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_GROWER;

public class GrainCorpApiService implements RemoteDataSource {

    private static GrainCorpApiService INSTANCE;

    private ApiService apiService;
    private String deviceToken = "";

    // Prevent direct instantiation.
    private GrainCorpApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public static GrainCorpApiService getInstance(ApiService apiService) {
        if (INSTANCE == null) {
            INSTANCE = new GrainCorpApiService(apiService);
        }
        return INSTANCE;
    }

    @Override
    public Observable<GlobalSettings> getGlobalSettings(String email, String platform, String appVersion, String deviceId, boolean secure) {
        Map<String, String> headers = new HashMap<>(8);
        String deviceType = android.os.Build.MODEL;
        headers.put("zDeviceType", deviceType);

        try {
            deviceToken = Tasks.await(FirebaseMessaging.getInstance().getToken());
        } catch (ExecutionException | InterruptedException e) {
            deviceToken = "";
        }

        if (TextUtils.isEmpty(deviceToken)) {
            deviceToken = "";
        }

        headers.put("zDeviceToken", deviceToken);
        headers.put("zEmailAddress", email);

        if (secure)
            return apiService.getGlobalSettingsSecured(headers, platform, appVersion, deviceId);

        return apiService.getGlobalSettings(headers, platform, appVersion, deviceId);
    }

    @Override
    public Observable<List<NotificationSettingsGroup>> getPushNotificationSettings(String deviceId, String email) {
        return apiService.getPushNotificationSettings(email, String.format("UserDeviceID eq \'%s\'", deviceId));
    }

    @Override
    public Observable<List<NotificationSettingsGroup>> setPushNotificationSettings(Map<String, Boolean> notificationSettingsChangedMap, String deviceId, String email) {
        Map<String, String> headers = new HashMap<>(8);
        String appVersion = BuildConfig.VERSION_NAME;
        String deviceType = android.os.Build.MODEL;

        headers.put("zPlatform", ApiConstants.PLATFORM);
        headers.put("zAppVersion", appVersion);
        headers.put("zDeviceType", deviceType);
        headers.put("zEmailAddress", email);

        return apiService.setPushNotificationSettings(headers, notificationSettingsChangedMap, String.format("UserDeviceID eq \'%s\'", deviceId));
    }

    @Override
    public Observable<List<MenuParentGroup>> getMenu(String deviceId, String userType, String accountNo, boolean isLoggedIn) {
        String queryString = String.format("UserType eq \'%s\' and AccountNo eq \'%s\'", userType, accountNo);
        if (isLoggedIn) {
            return apiService.getMenuSecured(queryString);
        } else {
            return apiService.getMenu(queryString);
        }
    }

    @Override
    public Observable<List<PushNotification>> getNotifications(String deviceId, String email) {
        return apiService.getNotifications(email, String.format("UserDeviceID eq \'%s\'", deviceId));
    }

    @Override
    public Observable<Response<Object>> setNotificationCount(String emailAddress, int pushNotificaitonCount, String deviceId) {
        return apiService.setPushNotificationsCount(emailAddress, pushNotificaitonCount, deviceId);
    }

    public Observable<List<OnboardingItem>> getOnboardingItems(String emailAddress, String deviceId) {
        return apiService.getOnboardingItems(emailAddress, String.format("UserdeviceId eq \'%s\'", deviceId));
    }

    @Override
    public Observable<OnboardingItem> seenOnboardingItem(String emailAddress, String deviceId, int item) {
        return apiService.seenOnboardingItem(emailAddress, deviceId, item);
    }

    @Override
    public Observable<Boolean> getTermsStatus() {
        return apiService.getTermsStatus().map(results -> {
            JsonObject result = results.getObject().getResults().get(0);
            return result.get("Accepted").getAsString().contains("X");
        });
    }

    private Observable<UserDetails> getUserDetails() {
        return apiService.getUserDetails();
    }

    private Observable<UserSettings> getUserSettings() {
        return apiService.getUserSettings();
    }

    private Observable<UserAccount> getUserAccount(@AccountGroupTypeDef String accountGroup) {
        return apiService.getUserAccount(String.format("AccountGroup eq \'%s\'", accountGroup));
    }

    @Override
    public Observable<UserInfo> getUserInfo() {
        return Observable.zip(getUserDetails(), getUserSettings(), getUserAccount(TYPE_GROWER), getUserAccount(TYPE_BUYER), (userDetails, userSettings, growerUserAccount, buyerUserAccount) -> {
            UserInfo userInfo = new UserInfo();
            if (userDetails.isGrowerFlag()) {
                userInfo.setGrower(true);
                userInfo.setAccountGroup(TYPE_GROWER);
                if (!TextUtils.isEmpty(userSettings.getGrowerSettings().getDefaultNgr())) {
                    userInfo.setNgrNo(userSettings.getGrowerSettings().getDefaultNgr());
                } else {
                    userInfo.setNgrNo(growerUserAccount.getAccountNo());
                }
            }

            if (userDetails.isBuyerFlag()) {
                userInfo.setBuyer(true);
                if (userInfo.getAccountGroup().isEmpty()) {
                    userInfo.setAccountGroup(TYPE_BUYER);
                }
                if (!TextUtils.isEmpty(userSettings.getBuyerSettings().getDefaultAcc())) {
                    userInfo.setAccountNo(userSettings.getBuyerSettings().getDefaultAcc());
                } else {
                    userInfo.setAccountNo(buyerUserAccount.getAccountNo());
                }
            }

            if (userDetails.isFreightPFlag()) {
                userInfo.setFreightProvider(true);
                if (userInfo.getAccountGroup().isEmpty()) {
                    userInfo.setAccountGroup(TYPE_EMPTY);
                }
            }
            return userInfo;
        });
    }

    @Override
    public Observable<List<Bid>> getBids(List<SearchFilter> searchFilters) {
        String requestBody = "";
        String crlf = "\r\n";
        MediaType mediaTypeMixed = MediaType.parse("multipart/mixed; boundary=batch");
        String multilineCrlf = crlf + crlf + crlf;

        for (int i = 0, searchFiltersSize = searchFilters.size(); i < searchFiltersSize; i++) {
            SearchFilter searchFilter = searchFilters.get(i);
            requestBody += "--batch" + crlf +
                    "Content-Type: application/http" + crlf +
                    "Content-Transfer-Encoding: binary" + multilineCrlf +
                    String.format("GET BestCashPriceSet(SiteNo='%s',Commodity='%s',Grade='%s')?$format=json HTTP/1.1",
                            searchFilter.getSite(), searchFilter.getCommodity(), searchFilter.getGrade()) + multilineCrlf;

            if (i == searchFiltersSize - 1) {
                requestBody += "--batch--" + crlf;
            }
        }

        return apiService.getBids(RequestBody.create(mediaTypeMixed, requestBody));
    }

    @Override
    public Observable<DeviceSettings> getDeviceSettings(String userDeviceId, String email, boolean secure) {

        userDeviceId = Util.quoteWrapString(userDeviceId);
        email = Util.quoteWrapString(email);
        if (TextUtils.isEmpty(email)) {
            return apiService.getDeviceSettings(userDeviceId);
        } else {
            if (secure) {
                return apiService.getDeviceSettingsSecured(userDeviceId, email);
            } else {
                return apiService.getDeviceSettings(userDeviceId, email);
            }
        }
    }

    @Override
    public Observable<DeviceSettings> deactivatePin(String userDeviceId, String email, boolean secure, CSRFToken csrfToken) {
        userDeviceId = Util.quoteWrapString(userDeviceId);
        email = Util.quoteWrapString(email);
        if (secure) {
            return apiService.deactivatePinSecured(csrfToken.csrfToken, userDeviceId, email);
        } else {
            return apiService.deactivatePin(userDeviceId, email);
        }
    }

    @Override
    public Observable<DeviceSettings> activatePin(String userDeviceId, @Nullable String pin, String email, boolean secure, CSRFToken csrfToken) {
        userDeviceId = Util.quoteWrapString(userDeviceId);
        email = Util.quoteWrapString(email);
        if (secure) {
            if (TextUtils.isEmpty(pin)) {
                pin = Util.quoteWrapString(""); //could be null so force it to be empty
                return apiService.activatePinWithExistingPinSecured(csrfToken.csrfToken, userDeviceId, pin, email);
            } else {
                pin = Util.quoteWrapString(pin);
                return apiService.activatePinSecured(csrfToken.csrfToken, userDeviceId, pin, email);
            }
        }
        return Observable.error(new SessionExpiredException());
    }

    @Override
    public Observable<DeviceSettings> checkPin(String userDeviceId, String pin, String email, boolean secure) {
        userDeviceId = Util.quoteWrapString(userDeviceId);
        email = Util.quoteWrapString(email);
        if (!secure) {
            pin = Util.quoteWrapString(pin);
            return apiService.checkPin(userDeviceId, email, pin);
        }
        return Observable.error(new Exception());
    }
}
