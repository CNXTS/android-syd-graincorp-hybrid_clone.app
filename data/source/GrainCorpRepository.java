package com.webling.graincorp.data.source;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.webling.graincorp.constants.SharedPrefsConstants;
import com.webling.graincorp.data.exception.MaxFailedPinLoginAttempts;
import com.webling.graincorp.data.source.local.LocalDataSource;
import com.webling.graincorp.data.source.remote.RemoteDataSource;
import com.webling.graincorp.encryption.AesGcmEncryption;
import com.webling.graincorp.encryption.GrainCorpKeyStore;
import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.model.DeviceSettings;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.MenuParentGroup;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.OnboardingItem;
import com.webling.graincorp.model.PushNotification;
import com.webling.graincorp.model.SearchFilter;
import com.webling.graincorp.model.UserInfo;
import com.webling.graincorp.model.UserStatus;
import com.webling.graincorp.rxbus.BehaviourRxBus;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.EventTypeDef;
import com.webling.graincorp.rxbus.event.GlobalEvent;
import com.webling.graincorp.util.WebViewCookieJar;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import retrofit2.Response;

import static com.webling.graincorp.constants.ApiConstants.CUSTOMER_ID;
import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_BUYER;
import static com.webling.graincorp.model.UserInfo.AccountGroupTypeDef.TYPE_GROWER;

@Singleton
public class GrainCorpRepository {
    @NonNull
    private final LocalDataSource localDataSource;
    private SharedPreferences sharedPreferences;
    private WebViewCookieJar webViewCookieJar;
    private Gson gson;
    private RxBus<GlobalEvent> globalEventRxBus;
    private BehaviourRxBus<GlobalEvent> globalEventBehaviourRxBus;
    private GrainCorpKeyStore keyStore;
    private AesGcmEncryption aesGcmEncryption;
    private final String TAG = getClass().getSimpleName();

    @NonNull
    private final RemoteDataSource remoteDataSource;

    @Inject
    public GrainCorpRepository(@NonNull RemoteDataSource remoteDataSource,
                               @NonNull LocalDataSource localDataSource,
                               SharedPreferences sharedPreferences,
                               WebViewCookieJar webViewCookieJar,
                               Gson gson,
                               RxBus<GlobalEvent> globalEventRxBus,
                               BehaviourRxBus<GlobalEvent> globalEventBehaviourRxBus,
                               GrainCorpKeyStore keyStore,
                               AesGcmEncryption aesGcmEncryption) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.sharedPreferences = sharedPreferences;
        this.webViewCookieJar = webViewCookieJar;
        this.gson = gson;
        this.globalEventRxBus = globalEventRxBus;
        this.globalEventBehaviourRxBus = globalEventBehaviourRxBus;
        this.keyStore = keyStore;
        this.aesGcmEncryption = aesGcmEncryption;
        setDeviceIdIfNotExists(UUID.randomUUID());
    }

    public Observable<List<PushNotification>> getNotificationsAsObservable() {
        return localDataSource.getAllNotificationsAsObservable();
    }

    public List<PushNotification> getNotifications() {
        return localDataSource.getAllNotifications();
    }

    public Observable<List<PushNotification>> fetchNotifications(String deviceId, String email, boolean areNotificationsEnabled) {
        return remoteDataSource.getNotifications(deviceId, email).map(pushNotifications -> {
            if (areNotificationsEnabled) {
                localDataSource.saveNotifications(pushNotifications);
            }
            return localDataSource.getAllNotifications();
        });
    }

    public Observable<GlobalSettings> getGlobalSettingsAsObservable() {
        return Observable.fromCallable(localDataSource::getGlobalSettings);
    }

    public GlobalSettings getGlobalSettings() {
        return localDataSource.getGlobalSettings();
    }

    public Observable<GlobalSettings> fetchGlobalSettings(String email, String platform, String appVersion, String deviceId, boolean secure) {
        return remoteDataSource.getGlobalSettings(email, platform, appVersion, deviceId, secure)
                .flatMap(globalSettings -> {
                    saveGlobalSettings(globalSettings);
                    return Observable.just(globalSettings);
                });
    }

    public void saveGlobalSettings(GlobalSettings globalSettings) {
        localDataSource.saveGlobalSettings(globalSettings);
        saveSessionCookieName(globalSettings.getSessionCookieName());
    }

    public void saveNotification(PushNotification notification) {
        localDataSource.saveNotification(notification);
    }

    public void deleteNotification(PushNotification notification) {
        localDataSource.deleteNotification(notification);
    }

    public DeviceId getDeviceId() {
        return localDataSource.getDeviceId();
    }

    public Observable<DeviceId> getDeviceIdObservable() {
        return Observable.just(true).map(aBoolean -> localDataSource.getDeviceId());
    }

    public void setDeviceIdIfNotExists(UUID uuid) {
        localDataSource.setDeviceIdIfNotExists(uuid);
    }

    public Observable<List<NotificationSettingsGroup>> getPushNotificationSettings(String deviceId, String email) {
        Observable<List<NotificationSettingsGroup>> pushNotificationSettingsRemote = remoteDataSource.getPushNotificationSettings(deviceId, email);

        return pushNotificationSettingsRemote.map(notificationSettingsGroups -> {
            localDataSource.savePushNotificationSettings(notificationSettingsGroups);
            return notificationSettingsGroups;
        });
    }

    public Observable<List<NotificationSettingsGroup>> getPushNotificationSettingsfromDB() {

        return getDeviceIdObservable().flatMap(deviceId -> localDataSource.getPushNotificationSettingsAsObservable(deviceId.uuid.toString()));
    }

    public Observable<List<NotificationSettingsGroup>> setPushNotificationSettings(Map<String, Boolean> notificationsSettingsChangedMap, String deviceId, String email) {
        Observable<List<NotificationSettingsGroup>> pushNotificationSettingsRemote = remoteDataSource.setPushNotificationSettings(notificationsSettingsChangedMap, deviceId, email);
        return pushNotificationSettingsRemote.map(notificationSettingsGroups -> {
            localDataSource.savePushNotificationSettings(notificationSettingsGroups);
            return localDataSource.getPushNotificationSettings(deviceId);
        });
    }

    public Observable<List<MenuParentGroup>> getMenu(String deviceId, String userType, String accountNo) {
        Observable<List<MenuParentGroup>> menuGroupsRemote = remoteDataSource.getMenu(deviceId, userType, accountNo, isLoggedInUser());
        return menuGroupsRemote.map(menuParentGroups -> {
            localDataSource.saveMenu(menuParentGroups);
            return localDataSource.getMenu(userType, accountNo);
        });
    }

    public Observable<Response<Object>> setNotificationCount(String emailAddress, int pushNotificaitonCount, String deviceId) {
        return remoteDataSource.setNotificationCount(emailAddress, pushNotificaitonCount, deviceId);
    }

    public String getEmail() {
        return sharedPreferences.getString(SharedPrefsConstants.SHARED_PREFS_SAVED_EMAIL, "");
    }

    public long getLoginTime() {
        return sharedPreferences.getLong(SharedPrefsConstants.SHARED_PREFS_SAVED_TIME, 0);
    }

    @UserStatus
    public int getUserStatus() {
        String email = getEmail();
        if (webViewCookieJar.hasSessionCookie(getSessionCookieName())) {
            return UserStatus.KNOWN;
        } else if (!email.isEmpty()) {
            return UserStatus.SEMI_KNOWN;
        } else {
            return UserStatus.ANONYMOUS;
        }
    }

    public boolean isLoggedInUser() {
        return getUserStatus() == UserStatus.KNOWN;
    }

    public boolean isSemiKnownUser() {
        return getUserStatus() == UserStatus.SEMI_KNOWN;
    }

    public boolean isAnonymousUser() {
        return getUserStatus() == UserStatus.ANONYMOUS;
    }

    @SuppressLint("ApplySharedPref")
    public void saveEmailAndTime(String email, long time) {
        //new user
        String previousUserEmail = getEmail();
        if (!previousUserEmail.isEmpty() && !email.equals(previousUserEmail)) {
            clearShowTermsAfterLogin();
            clearPinOnboardingShown();
            clearInterimPassword();
            clearPassword();
            clearFailedPinLoginAttempts();
            clearDoNotShowPinOnboarding();
            clearForceShowEmailPasswordLogin();
            clearPinOnboardingPostponeMillis();
            keyStore.clearKey();

            localDataSource.deleteAllNotifications();
            globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_REMOVE_BADGE_COUNT));
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPrefsConstants.SHARED_PREFS_SAVED_EMAIL, email);
        editor.putLong(SharedPrefsConstants.SHARED_PREFS_SAVED_TIME, time);
        editor.commit();
    }

    public boolean saveInterimPassword(String password) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String encryptedPassword = keyStore.encrypt(password);
            editor.putString(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD_INTERIM, encryptedPassword);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getInterimPassword() {
        String encryptedPassword = sharedPreferences.getString(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD_INTERIM, "");
        try {
            return keyStore.decrypt(encryptedPassword);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearInterimPassword() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD_INTERIM);
        editor.apply();
        keyStore.clearKey();
    }

    public void savePassword(String password, String key) throws Exception {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String encryptedPassword = aesGcmEncryption.encrypt(key, password);
        editor.putString(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD, encryptedPassword);
        editor.apply();
    }

    public String getPassword(@NonNull String key) throws MaxFailedPinLoginAttempts {
        String encryptedPassword = sharedPreferences.getString(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD, "");
        try {
            return aesGcmEncryption.decrypt(key, encryptedPassword);
        } catch (Exception e) {
            if (e instanceof InvalidKeyException) {
                incrementFailedPinLoginAttempts();
            }
        }
        return null;
    }

    public void clearPassword() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_SAVED_PASSWORD);
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public void saveSessionCookieName(String cookieName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPrefsConstants.SHARED_PREFS_SESSION_COOKIE_NAME, cookieName);
        editor.commit();
    }

    public String getSessionCookieName() {
        return sharedPreferences.getString(SharedPrefsConstants.SHARED_PREFS_SESSION_COOKIE_NAME, "");
    }


    public Observable<UserInfo> getUserInfo() {
        if (!isLoggedInUser()) return Observable.just(new UserInfo());
        return remoteDataSource.getUserInfo()
                .doOnNext(localDataSource::saveUserInfo)
                .flatMap(user -> remoteDataSource.getTermsStatus())
                .flatMap(termsAccepted -> {
                    UserInfo userInfo = localDataSource.getUserInfo();
                    userInfo.setTermsAccepted(termsAccepted);
                    localDataSource.saveUserInfo(userInfo);
                    return Observable.just(userInfo);
                });
    }


    public UserInfo getUserInfofromDB() {
        UserInfo userInfo = localDataSource.getUserInfo();
        return userInfo == null ? new UserInfo() : userInfo;
    }

    public Observable<UserInfo> getUserInfofromDBAsObservable() {
        return Observable.fromCallable(this::getUserInfofromDB);
    }

    public void switchRole() {
        UserInfo userInfo = getUserInfofromDB();
        @UserInfo.AccountGroupTypeDef String switchableAccountGroup = userInfo.getSwitchableAccountGroup();
        if (!TextUtils.isEmpty(switchableAccountGroup)) {
            localDataSource.setUserType(switchableAccountGroup);
        }
    }

    public void logout() {
        clearSessionCookie();
        localDataSource.clearUserInfo();
    }

    public void clearSessionCookie() {
        webViewCookieJar.clearSessionCookie();
    }

    public Observable<List<OnboardingItem>> getOnBoardingItems(String deviceId) {
        return remoteDataSource.getOnboardingItems(getEmail(), deviceId);
    }

    public Observable<OnboardingItem> seenOnBoardingItem(String deviceId, int page) {
        return remoteDataSource.seenOnboardingItem(getEmail(), deviceId, page);
    }

    public void saveSearchFilter(String searchFilterJson) {
        if (TextUtils.isEmpty(searchFilterJson)) return;

        try {
            JsonObject jo = gson.fromJson(searchFilterJson, JsonObject.class);
            if (jo.has("value")) {
                JsonArray valueArray = jo.get("value").getAsJsonArray();
                List<SearchFilter> searchFilters = new ArrayList<>(valueArray.size());
                for (JsonElement elem : valueArray) {
                    searchFilters.add(gson.fromJson(elem, SearchFilter.class));
                }
                setSavingSearchFilters(true);
                localDataSource.saveSearchFilters(searchFilters);
            }
        } catch (JsonSyntaxException | ClassCastException e) {
            Log.e(TAG, "Exception", e);
        } finally {
            setSavingSearchFilters(false);
            //DBFlow's FlowContentObserver gets called for each item regardless of batching, so use EventBus to notify
            globalEventBehaviourRxBus.send(new GlobalEvent(EventTypeDef.SEARCH_FILTERS_SAVED));
        }
    }

    public Observable<List<SearchFilter>> getSearchFilters() {
        return Observable.fromCallable(localDataSource::getSearchFilters);
    }

    public void saveBids(List<Bid> bids) {
        localDataSource.saveBids(bids);
    }

    public Boolean getSearchFilterListSize(){
        return localDataSource.getSearchFilters().size() > 0;
    }

    public Observable<List<Bid>> getBids() {
        return getSearchFilters()
                .map(bool -> localDataSource.getSearchFilters())
                .flatMap(remoteDataSource::getBids, Pair::new)
                .map(searchFilterBidListPair -> {
                    List<SearchFilter> searchFilterList = searchFilterBidListPair.first;
                    List<Bid> bidList = searchFilterBidListPair.second;
                    Iterator<SearchFilter> searchFilterIterator = searchFilterList.iterator();
                    Iterator<Bid> bidIterator = bidList.iterator();

                    while (searchFilterIterator.hasNext() && bidIterator.hasNext()) {
                        Bid bid = bidIterator.next();
                        SearchFilter searchFilter = searchFilterIterator.next();
                        if (bid.isErrorState()) { //error, so get partial bid values from search filter
                            bid.setSiteNo(searchFilter.getSite());
                            bid.setSiteName(searchFilter.getSiteDescription());
                            bid.setCommodity(searchFilter.getCommodity());
                            bid.setCommodityDesc(searchFilter.getCommodityDescription());
                            bid.setGrade(searchFilter.getGrade());
                        }
                    }

                    saveBids(bidList);
                    return localDataSource.getBids();
                });
    }

    public Observable<DeviceSettings> getDeviceSettings() {
        Observable<DeviceSettings> deviceSettingsRemote = remoteDataSource.getDeviceSettings(getDeviceId().uuid.toString(), getEmail(), isLoggedInUser());
        return deviceSettingsRemote.map(deviceSettings -> {
            localDataSource.saveDeviceSettings(deviceSettings);
            return deviceSettings; // getting from local wont give us the key since its not in the model anymore
        });
    }

    public Observable<DeviceSettings> getDeviceSettingsFromDB() {
        return Observable.fromCallable(localDataSource::getDeviceSettings);
    }

    public CSRFToken getCsrfToken() {
        return localDataSource.getCsrfToken();
    }

    public void setCsrfToken(CSRFToken csrfToken) {
        localDataSource.setCsrfToken(csrfToken);
    }

    public Observable<DeviceSettings> deactivatePin() {
        Observable<DeviceSettings> deviceSettingsRemote = remoteDataSource.deactivatePin(getDeviceId().uuid.toString(), getEmail(), isLoggedInUser(), getCsrfToken());
        return deviceSettingsRemote.map(deviceSettings -> {
            localDataSource.saveDeviceSettings(deviceSettings);
            return deviceSettings; // getting from local wont give us the key since its not in the model anymore
        });
    }

    public Observable<DeviceSettings> activatePin() {
        Observable<DeviceSettings> deviceSettingsRemote = remoteDataSource.activatePin(getDeviceId().uuid.toString(), "", getEmail(), isLoggedInUser(), getCsrfToken());
        return deviceSettingsRemote.map(deviceSettings -> {
            localDataSource.saveDeviceSettings(deviceSettings);
            return deviceSettings; // getting from local wont give us the key since its not in the model anymore
        });
    }

    public Observable<DeviceSettings> activatePin(String pin) {
        Observable<DeviceSettings> deviceSettingsRemote = remoteDataSource.activatePin(getDeviceId().uuid.toString(), pin, getEmail(), isLoggedInUser(), getCsrfToken());
        return deviceSettingsRemote.map(deviceSettings -> {
            localDataSource.saveDeviceSettings(deviceSettings);
            return deviceSettings; // getting from local wont give us the key since its not in the model anymore
        });
    }

    public Observable<DeviceSettings> checkPin(String pin) {
        Observable<DeviceSettings> deviceSettingsRemote = remoteDataSource.checkPin(getDeviceId().uuid.toString(), pin, getEmail(), isLoggedInUser());
        return deviceSettingsRemote.map(deviceSettings -> {
            localDataSource.saveDeviceSettings(deviceSettings);
            return deviceSettings; // getting from local wont give us the key since its not in the model anymore
        });
    }

    @SuppressLint("ApplySharedPref")
    public void setSavingSearchFilters(boolean isSaving) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPrefsConstants.SHARED_PREFS_SAVING_SEARCH_FILTERS, isSaving);
        editor.commit();
    }

    public boolean isSavingSearchFilters() {
        return sharedPreferences.getBoolean(SharedPrefsConstants.SHARED_PREFS_SAVING_SEARCH_FILTERS, false);
    }

    @SuppressLint("ApplySharedPref")
    public void setLastActiveTime(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SharedPrefsConstants.SHARED_PREFS_LAST_ACTIVE_TIME, time);
        editor.commit();
    }

    public void resetLastActiveTime() {
        setLastActiveTime(0);
    }

    public long getLastActiveTime() {
        return sharedPreferences.getLong(SharedPrefsConstants.SHARED_PREFS_LAST_ACTIVE_TIME, 0);
    }

    public void setPinOnboardingPostponeMillis(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_POSTPONE_MILLIS, time);
        editor.apply();
    }

    public long getPinOnboardingPostponeMillis() {
        return sharedPreferences.getLong(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_POSTPONE_MILLIS, 0);
    }

    public void clearPinOnboardingPostponeMillis() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_POSTPONE_MILLIS);
        editor.apply();
    }

    public void setDoNotShowPinOnboarding() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPrefsConstants.SHARED_PREFS_DO_NOT_SHOW_PIN_ONBOARDING, true);
        editor.apply();
    }

    public boolean shouldNotShowPinOnboarding() {
        return sharedPreferences.getBoolean(SharedPrefsConstants.SHARED_PREFS_DO_NOT_SHOW_PIN_ONBOARDING, false);
    }

    public void clearDoNotShowPinOnboarding() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_DO_NOT_SHOW_PIN_ONBOARDING);
        editor.apply();
    }

    public void setPinOnboardingShown() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_SHOWN, true);
        editor.apply();
    }

    public boolean getPinOnboardingShown() {
        return sharedPreferences.getBoolean(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_SHOWN, false);
    }

    public void clearPinOnboardingShown() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_PIN_ONBOARDING_SHOWN);
        editor.apply();
    }

    public void incrementFailedPinLoginAttempts() throws MaxFailedPinLoginAttempts {
        int failedPinLoginAttempts = getFailedPinLoginAttempts() + 1;
        setFailedPinLoginAttempts(failedPinLoginAttempts);

        if (failedPinLoginAttempts >= getGlobalSettings().getMaxPinFail()) {
            clearPassword();
            setForceShowEmailPasswordLogin();
            throw new MaxFailedPinLoginAttempts();
        }
    }

    public void setFailedPinLoginAttempts(int attempts) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SharedPrefsConstants.SHARED_PREFS_FAILED_PIN_LOGIN_ATTEMPTS, attempts);
        editor.apply();
    }

    public int getFailedPinLoginAttempts() {
        return sharedPreferences.getInt(SharedPrefsConstants.SHARED_PREFS_FAILED_PIN_LOGIN_ATTEMPTS, 0);
    }

    public void clearFailedPinLoginAttempts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_FAILED_PIN_LOGIN_ATTEMPTS);
        editor.apply();
    }

    public void setForceShowEmailPasswordLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPrefsConstants.SHARED_PREFS_FORCE_SHOW_EMAIL_PASSWORD_LOGIN, true);
        editor.apply();
    }

    public boolean getForceShowEmailPasswordLogin() {
        return sharedPreferences.getBoolean(SharedPrefsConstants.SHARED_PREFS_FORCE_SHOW_EMAIL_PASSWORD_LOGIN, false);
    }

    public void clearForceShowEmailPasswordLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_FORCE_SHOW_EMAIL_PASSWORD_LOGIN);
        editor.apply();
    }

    public void setShowTermsAfterLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SharedPrefsConstants.SHARED_PREFS_SHOW_TERMS_AFTER_LOGIN, true);
        editor.apply();
    }

    public boolean getShowTermsAfterLogin() {
        return sharedPreferences.getBoolean(SharedPrefsConstants.SHARED_PREFS_SHOW_TERMS_AFTER_LOGIN, false);
    }

    public void clearShowTermsAfterLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SharedPrefsConstants.SHARED_PREFS_SHOW_TERMS_AFTER_LOGIN);
        editor.apply();
    }

    public void setUserNotifyType(String actionUrl){
        Uri uri = Uri.parse(actionUrl);
        String customerId = uri.getQueryParameter(CUSTOMER_ID);
        if(actionUrl.contains(TYPE_GROWER) && !customerId.isEmpty()){
            updateGrowerData(customerId);
        }else if(actionUrl.contains(TYPE_BUYER) && !customerId.isEmpty()){
            updateBuyerData(customerId);
        }
    }

    private void updateBuyerData(String customerId){
        //update buyer model
        UserInfo userInfo = new UserInfo();
        userInfo.setBuyer(true);
        userInfo.setAccountNo(customerId);
        userInfo.setAccountGroup(TYPE_BUYER);

        //update buyer data to db
        UserInfo userDbData =  localDataSource.getUserInfo();
        if(userDbData != null){
            userDbData.setBuyer(true);
            userDbData.setAccountNo(customerId);
            userDbData.setAccountGroup(TYPE_BUYER);

            localDataSource.saveUserInfo(userDbData);
        }
    }

    private void updateGrowerData(String customerId){
        //update grower model
        UserInfo userInfo = new UserInfo();
        userInfo.setGrower(true);
        userInfo.setNgrNo(customerId);
        userInfo.setAccountGroup(TYPE_GROWER);

        //update grower data to db
        UserInfo userDbData = localDataSource.getUserInfo();
        if(userDbData != null){
            userDbData.setGrower(true);
            userDbData.setNgrNo(customerId);
            userDbData.setAccountGroup(TYPE_GROWER);

            localDataSource.saveUserInfo(userDbData);
        }
    }

}