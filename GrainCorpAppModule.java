package com.webling.graincorp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.webling.graincorp.constants.SharedPrefsConstants;
import com.webling.graincorp.data.api.ApiService;
import com.webling.graincorp.data.api.HostSelectionInterceptor;
import com.webling.graincorp.data.api.XCsrfTokenInterceptor;
import com.webling.graincorp.data.api.converters.NestedDenvelopingConverterFactory;
import com.webling.graincorp.data.api.converters.SingleDenvelopingConverterFactory;
import com.webling.graincorp.data.exception.MaintenanceModeException;
import com.webling.graincorp.data.exception.SessionExpiredException;
import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;
import com.webling.graincorp.data.source.local.LocalDataSource;
import com.webling.graincorp.data.source.remote.GrainCorpApiService;
import com.webling.graincorp.data.source.remote.RemoteDataSource;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.provider.AnalyticsProvider;
import com.webling.graincorp.provider.AnalyticsProviderImpl;
import com.webling.graincorp.rxbus.BehaviourRxBus;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.GlobalEvent;
import com.webling.graincorp.util.WebViewCookieJar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.webling.graincorp.constants.ApiConstants.GC_USER_AGENT;
import static com.webling.graincorp.constants.ApiConstants.HEADER_LOCATION;

@Module
public class GrainCorpAppModule {

    private Application application;
    private volatile LocalDataSource dataSource;

    public GrainCorpAppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context providesApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    RemoteDataSource provideGrainCorpApiService(ApiService apiService) {
        return GrainCorpApiService.getInstance(apiService);
    }

    @Provides
    @Singleton
    LocalDataSource provideGrainCorpDatabase(RxBus<GlobalEvent> globalEventRxBus, CSRFToken csrfToken) {
        return new GrainCorpDatabase(FlowManager.getDatabase((GrainCorpDatabase.class)), globalEventRxBus, csrfToken);
    }

    @Provides
    @Singleton
    ApiService provideRetrofitApiService(OkHttpClient client, GsonConverterFactory gsonConverterFactory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new NestedDenvelopingConverterFactory(gsonConverterFactory))
                .addConverterFactory(new SingleDenvelopingConverterFactory())
                .addConverterFactory(gsonConverterFactory)//last!
                .build();
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(WebViewCookieJar cookieJar, LocalDataSource dataSource, HostSelectionInterceptor hostSelectionInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            builder.addNetworkInterceptor(interceptor);
        }

        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("User-Agent", GC_USER_AGENT)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }).addNetworkInterceptor(chain -> {
            Request original = chain.request();
            Response response = chain.proceed(original);
            String responseLocationHeader = response.header(HEADER_LOCATION);
            GlobalSettings globalSettings = dataSource.getGlobalSettings();
            String maintenanceUrl = globalSettings != null ? globalSettings.getMaintenanceUrl() : null;
            if (globalSettings != null && response.request().url().toString().contains(globalSettings.getLoginUrl())) {
                throw new SessionExpiredException();
            }else if (responseLocationHeader != null && responseLocationHeader.equals(maintenanceUrl)) {
                throw new MaintenanceModeException();
            } else {
                return response;
            }
        }).addInterceptor(hostSelectionInterceptor)
                .addInterceptor(new XCsrfTokenInterceptor(dataSource));

        builder.cookieJar(cookieJar);

        return builder.build();
    }

    @Provides
    @Singleton
    HostSelectionInterceptor providesHostSelectionInterceptor(LocalDataSource localDataSource) {
        HostSelectionInterceptor hostSelectionInterceptor = new HostSelectionInterceptor();
        hostSelectionInterceptor.setDataSource(localDataSource);
        return hostSelectionInterceptor;
    }

    @Provides
    @Singleton
    Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    GsonConverterFactory providesGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    WebViewCookieJar providesWebviewCookieJar(CookieManager cookieManager, CookieSyncManager cookieSyncManager) {
        return new WebViewCookieJar(cookieManager, cookieSyncManager);
    }

    @Provides
    @Singleton
    CookieSyncManager providesCookieSyncManager(Context context) {
        return CookieSyncManager.createInstance(context);
    }

    @Provides
    @Singleton
    CookieManager providesCookieManager() {
        return CookieManager.getInstance();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences() {
        return application.getSharedPreferences(SharedPrefsConstants.SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    AnalyticsProvider providesAnalyticsProvider(Context context) {
        return new AnalyticsProviderImpl(
                FirebaseAnalytics.getInstance(context));
    }

    //TODO make this async friendly?
    @Provides
    @Singleton
    DeviceId provideDeviceId(GrainCorpRepository repository) {
        return repository.getDeviceId();
    }

    @Provides
    @Singleton
    RxBus<GlobalEvent> provideGlobalEventRxBus() {
        return new RxBus<>();
    }

    @Provides
    @Singleton
    BehaviourRxBus<GlobalEvent> provideGlobalEventBehaviourRxBus() {
        return new BehaviourRxBus<>();
    }
}
