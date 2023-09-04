package com.webling.graincorp.data.api;

import com.google.gson.JsonObject;
import com.webling.graincorp.data.api.converters.nested.MenuDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.NotificationSettingsDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.OnboardingDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.PushNotificationDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.UserAccountDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.UserDetailsDenvelopingConverter;
import com.webling.graincorp.data.api.converters.single.BidDenvelopingConverter;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.NestedResultsResponse;
import com.webling.graincorp.data.api.model.response.SingleResultsResponse;
import com.webling.graincorp.data.api.model.response.user.UserAccount;
import com.webling.graincorp.data.api.model.response.user.UserDetails;
import com.webling.graincorp.data.api.model.response.user.UserSettings;
import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.DeviceSettings;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.MenuParentGroup;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.OnboardingItem;
import com.webling.graincorp.model.PushNotification;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.webling.graincorp.constants.ApiConstants.X_CSRF_TOKEN_HEADER;
import static com.webling.graincorp.constants.ApiConstants.X_CSRF_TOKEN_FETCH_HEADER;

/**
 * Caution when using {@link retrofit2.http.Url} and any other HTTP Methods which resolve against the full URL as there is a dynamic host interceptor.
 *
 * @see HostSelectionInterceptor
 */
public interface ApiService {
    @GET("sap/opu/odata/SAP/CCGLOBAL_PUBLIC/GlobalSettingsAppSet(Platform='{platform}',AppVersion='{appVersion}',UserDeviceID='{deviceId}')?$format=json")
    @SingleResultsResponse
    Observable<GlobalSettings> getGlobalSettings(@HeaderMap Map<String, String> deviceDetails, @Path("platform") String platform, @Path("appVersion") String appVersion, @Path("deviceId") String deviceId);

    @GET("sap/opu/odata/SAP/GLOBALSETTINGS/GlobalSettingsAppSet(Platform='{platform}',AppVersion='{appVersion}',UserDeviceID='{deviceId}')?$format=json")
    @SingleResultsResponse
    Observable<GlobalSettings> getGlobalSettingsSecured(@HeaderMap Map<String, String> deviceDetails, @Path("platform") String platform, @Path("appVersion") String appVersion, @Path("deviceId") String deviceId);

    @GET("sap/opu/odata/sap/SEPNPUBLIC/SettingsGroupSet?$expand=SettingsGroupItemSet&$format=json")
    @NestedResultsResponse(converter = NotificationSettingsDenvelopingConverter.class)
    Observable<List<NotificationSettingsGroup>> getPushNotificationSettings(@Header("zEmailAddress") String emailAddress, @Query(value = "$filter", encoded = true) String deviceIdWithFilterString);

    @GET("sap/opu/odata/sap/SEPNPUBLIC/SettingsGroupSet?$expand=SettingsGroupItemSet&$format=json")
    @NestedResultsResponse(converter = NotificationSettingsDenvelopingConverter.class)
    Observable<List<NotificationSettingsGroup>> setPushNotificationSettings(@HeaderMap Map<String, String> deviceDetails,
                                                                            @HeaderMap Map<String, Boolean> notificationsSettingsChanged,
                                                                            @Query(value = "$filter", encoded = true) String deviceIdWithFilterString);

    @GET("sap/opu/odata/sap/SECCMENU/MenuParentSet?$expand=MenuChildSet&$format=json")
    @NestedResultsResponse(converter = MenuDenvelopingConverter.class)
    Observable<List<MenuParentGroup>> getMenuSecured(@Query(value = "$filter", encoded = true) String userTypeAndAccountNoWithFilterString);

    @GET("sap/opu/odata/sap/SECCMENUPUBLIC/MenuParentSet?$expand=MenuChildSet&$format=json")
    @NestedResultsResponse(converter = MenuDenvelopingConverter.class)
    Observable<List<MenuParentGroup>> getMenu(@Query(value = "$filter", encoded = true) String userTypeAndAccountNoWithFilterString);

    @GET("sap/opu/odata/sap/SEPNPUBLIC/PushNotificationsSet?$format=json")
    @NestedResultsResponse(converter = PushNotificationDenvelopingConverter.class)
    Observable<List<PushNotification>> getNotifications(@Header("zEmailAddress") String emailAddress,
                                                        @Query(value = "$filter", encoded = true) String userDeviceIdWithFilterString);

    @GET("sap/opu/odata/SAP/SEPNPUBLIC/PNCountSet(UserDeviceID='{deviceId}')?$format=json")
    Observable<Response<Object>> setPushNotificationsCount(@Header("zEmailAddress") String emailAddress, @Header("zPNCount") int pushNotificationCount, @Path("deviceId") String deviceId);

    @GET("sap/opu/odata/sap/seonboard_pub/CCAppOnboardingSet?$format=json")
    @NestedResultsResponse(converter = OnboardingDenvelopingConverter.class)
    Observable<List<OnboardingItem>> getOnboardingItems(@Header("zEmailAddress") String emailAddress, @Query(value = "$filter", encoded = true) String deviceIdWithFilterString);


    @GET("sap/opu/odata/sap/seonboard_pub/CCAppOnboardingSet(UserdeviceId='{userDeviceId}',OnboardPage={onboardingPage})?$format=json")
    @SingleResultsResponse
    Observable<OnboardingItem> seenOnboardingItem(@Header("zEmailAddress") String emailAddress, @Path("userDeviceId") String userDeviceId, @Path("onboardingPage") int onboardingPage);

    @GET("sap/opu/odata/SAP/SECURITY/UserDetailsSet?$top=2000&$format=json")
    @NestedResultsResponse(converter = UserDetailsDenvelopingConverter.class)
    Observable<UserDetails> getUserDetails();

    @GET("sap/opu/odata/SAP/USERSETTINGS/UserSet('')?$expand=GrowerSettingsSet,BuyerSettingsSet&$format=json")
    @SingleResultsResponse
    Observable<UserSettings> getUserSettings();

    @GET("sap/opu/odata/SAP/SECURITY/SecuritySet?$top=1&$format=json")
    @NestedResultsResponse(converter = UserAccountDenvelopingConverter.class)
    Observable<UserAccount> getUserAccount(@Query(value = "$filter", encoded = true) String accountGroupWithUserType);

    @Headers({"Accept: application/json"})
    @POST("sap/opu/odata/SAP/BID_PUBLIC/$batch")
    @SingleResultsResponse(converter = BidDenvelopingConverter.class)
    Observable<List<Bid>> getBids(@Body RequestBody batchRequestBody);

    @GET("sap/opu/odata/SAP/TERMS_CONDS/TermsCondsSet?$top=2000&$format=json")
    Observable<NestedEnvelope<JsonObject>> getTermsStatus();

    @GET("sap/opu/odata/sap/SEDEVICE_PUBLIC/Settings?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> getDeviceSettings(@Query(value = "ID") String userDeviceId);

    @GET("sap/opu/odata/sap/SEDEVICE_PUBLIC/Settings?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> getDeviceSettings(@Query(value = "ID") String userDeviceId, @Query("Email") String userEmail);

    @GET("sap/opu/odata/sap/SEDEVICE/Settings?$format=json")
    @SingleResultsResponse
    @Headers(X_CSRF_TOKEN_FETCH_HEADER)
    Observable<DeviceSettings> getDeviceSettingsSecured(@Query(value = "ID") String userDeviceId, @Query("Email") String userEmail);

    @GET("sap/opu/odata/sap/SEDEVICE_PUBLIC/DeactivatePin?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> deactivatePin(@Query(value = "ID") String userDeviceId, @Query("Email") String userEmail);

    @GET("sap/opu/odata/sap/SEDEVICE/DeactivatePin?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> deactivatePinSecured(@Header(X_CSRF_TOKEN_HEADER) String csrfToken, @Query(value = "ID") String userDeviceId, @Query("Email") String userEmail);

    @POST("sap/opu/odata/sap/SEDEVICE/ActivatePin?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> activatePinSecured(@Header(X_CSRF_TOKEN_HEADER) String csrfToken, @Query(value = "ID") String userDeviceId, @Query("Pin") String pin, @Query("Email") String userEmail);

    @POST("sap/opu/odata/sap/SEDEVICE/ActivatePin?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> activatePinWithExistingPinSecured(@Header(X_CSRF_TOKEN_HEADER) String csrfToken, @Query(value = "ID") String userDeviceId, @Query("Pin") String pin, @Query("Email") String userEmail);

    @GET("sap/opu/odata/sap/SEDEVICE_PUBLIC/CheckPin?$format=json")
    @SingleResultsResponse
    Observable<DeviceSettings> checkPin(@Query(value = "ID") String userDeviceId, @Query("Email") String userEmail, @Query("Pin") String pin);

}
