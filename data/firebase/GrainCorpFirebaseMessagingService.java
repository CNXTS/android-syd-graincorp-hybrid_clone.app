package com.webling.graincorp.data.firebase;

import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.webling.graincorp.GrainCorpApp;
import com.webling.graincorp.data.exception.SessionExpiredException;
import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.model.DeviceId;
import com.webling.graincorp.model.UserInfo;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.EventTypeDef;
import com.webling.graincorp.rxbus.event.FirebaseMessagePayload;
import com.webling.graincorp.rxbus.event.GlobalEvent;

import javax.inject.Inject;

import io.reactivex.Observable;

public class GrainCorpFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";

    @Inject
    RxBus<GlobalEvent> globalEventRxBus;

    @Inject
    GrainCorpRepository repository;

    @Inject
    DeviceId deviceId;


    public GrainCorpFirebaseMessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerFirebaseServiceComponent.builder()
                .grainCorpAppComponent(((GrainCorpApp) getApplication()).getComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String deviceIdString = deviceId.uuid.toString();
        UserInfo userInfo = repository.getUserInfofromDB();
        Observable.zip(repository.getDeviceSettings(),
                repository.getMenu(deviceIdString, userInfo.getAccountGroup(), userInfo.getRelevantAccountNumber()),
                repository.getPushNotificationSettings(deviceIdString, repository.getEmail()),
                (deviceSettings, menuParentGroups, pns) -> deviceSettings)
                .doOnNext(deviceSettings -> globalEventRxBus.send(new GlobalEvent(new FirebaseMessagePayload(deviceSettings.getPnCount(), NotificationManagerCompat.from(this).areNotificationsEnabled()), EventTypeDef.TYPE_FIREBASE_MESSAGE_RECEIVED)))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof SessionExpiredException) {
                        globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_LOGOUT));
                    }
                    return Observable.empty();
                })
                .subscribe();

    }
    
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

}
