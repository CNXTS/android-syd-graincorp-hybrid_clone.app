package com.webling.graincorp;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.encryption.AesGcmEncryption;
import com.webling.graincorp.encryption.GrainCorpKeyStore;
import com.webling.graincorp.model.CSRFToken;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.provider.AnalyticsProvider;
import com.webling.graincorp.rxbus.BehaviourRxBus;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.GlobalEvent;
import com.webling.graincorp.util.WebViewCookieJar;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = GrainCorpAppModule.class)
public interface GrainCorpAppComponent {
    GrainCorpRepository graincorpRepository();

    SharedPreferences sharedPreferences();

    DeviceId deviceId();

    RxBus<GlobalEvent> globalEventRxBus();

    BehaviourRxBus<GlobalEvent> globalEventBehaviourRxBus();

    Gson gson();

    WebViewCookieJar webviewCookieJar();

    OkHttpClient okHttpClient();

    AnalyticsProvider analyticsProvider();

    CSRFToken csrfToken();

    GrainCorpKeyStore keyStore();

    AesGcmEncryption aesGcmEncryption();
}
