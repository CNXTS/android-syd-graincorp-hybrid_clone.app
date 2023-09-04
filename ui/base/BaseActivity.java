package com.webling.graincorp.ui.base;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.webling.graincorp.BuildConfig;
import com.webling.graincorp.GrainCorpApp;
import com.webling.graincorp.R;
import com.webling.graincorp.constants.Analytics;
import com.webling.graincorp.constants.ApiConstants;
import com.webling.graincorp.data.exception.MaintenanceModeException;
import com.webling.graincorp.data.exception.SessionExpiredException;
import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.model.UserInfo;
import com.webling.graincorp.provider.AnalyticsProvider;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.EventTypeDef;
import com.webling.graincorp.rxbus.event.FirebaseMessagePayload;
import com.webling.graincorp.rxbus.event.GlobalEvent;
import com.webling.graincorp.ui.onboarding.OnboardingActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

import static com.webling.graincorp.util.RxUtil.applySchedulers;

public abstract class BaseActivity extends AppCompatActivity implements ComponentCallbacks2 {

    public static final int ACTIVITY_FINISHED = 99;

    AlertDialog dialog;
    private CompositeDisposable disposables;
    private AlertDialog.Builder builder;
    AlertDialog maintenanceDialog;
    private AlertDialog.Builder maintenanceBuilder;
    private ImageView imageView;
    private boolean firstLaunch = true, resumedFromBackground = true;
    private boolean onboardingShown;

    @Inject
    GrainCorpRepository grainCorpRepository;

    @Inject
    AnalyticsProvider analyticsProvider;

    @Inject
    RxBus<GlobalEvent> globalEventRxBus;
    private BroadcastReceiver screenOnOffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerBaseComponent.builder()
                .grainCorpAppComponent(((GrainCorpApp) getApplication()).getComponent())
                .build()
                .inject(this);

        imageView = new ImageView(this);

        registerScreenOffReceiver();

        disposables = new CompositeDisposable();

    }

    /**
     * Used to detect when the app is in foreground but the screen is locked.
     * To detect inactivity the last active time is also saved here when the device's screen goes off
     */
    private void registerScreenOffReceiver() {

        final IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();
                //The app could be in background and the screen could then be turned off, so only set the last active time if not already set
                if (strAction.equals(Intent.ACTION_SCREEN_OFF) && grainCorpRepository.getLastActiveTime() == 0) {
                    grainCorpRepository.setLastActiveTime(SystemClock.elapsedRealtime());
                }
            }
        };

        getApplicationContext().registerReceiver(screenOnOffReceiver, screenOffFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle(getString(R.string.offline_dialog_title))
                .setMessage(getString(R.string.offline_dialog_message));
        builder.setPositiveButton(getString(R.string.offline_dialog_positive_button), (dialog1, which) -> restrictUser());
        dialog = builder.create();

        maintenanceAlert();

        disposables.add(globalEventRxBus.register(EventTypeDef.TYPE_SHOW_OFFLINE_DIALOG)
                .compose(applySchedulers())
                .subscribe(event -> showDialog(false), err -> {
                }));

        disposables.add(globalEventRxBus.register(EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_SHOW)
                .compose(applySchedulers())
                .subscribe(event -> showMaintenanceDialog(false), err -> {
                }));

        if (resumedFromBackground) {
            analyticsProvider.trackEvent(Analytics.CustomEvents.APP_FOREGROUNDED);
        }
        restrictUser();
        resumedFromBackground = true;
    }

    //TODO Needs refactor
    void restrictUser() {
        if (dialog != null)
            dialog.dismiss();

        disposables.add(grainCorpRepository.getDeviceIdObservable()
                .flatMap(deviceId -> {
                    if (firstLaunch) {
                        return grainCorpRepository.fetchGlobalSettings(grainCorpRepository.getEmail(),
                                ApiConstants.PLATFORM,
                                BuildConfig.VERSION_NAME,
                                deviceId.uuid.toString(),
                                grainCorpRepository.isLoggedInUser());
                    } else {
                        return grainCorpRepository.getGlobalSettingsAsObservable();
                    }
                })
                .flatMap(globalSettings -> grainCorpRepository.getDeviceSettings())
                .flatMap(deviceSettings -> {

                    globalEventRxBus.send(new GlobalEvent(
                            new FirebaseMessagePayload(deviceSettings.getPnCount(),
                                    NotificationManagerCompat.from(BaseActivity.this).areNotificationsEnabled()),
                            EventTypeDef.TYPE_FIREBASE_MESSAGE_RECEIVED));

                    // Forced update
                    if (deviceSettings.isAppUpdateRequired()) {
                        String appUpdDescription = deviceSettings.getAppUpdateDescription();
                        if (TextUtils.isEmpty(appUpdDescription)) {
                            appUpdDescription = getString(R.string.forced_update_dialog_body);
                        }
                        builder.setCancelable(false)
                                .setTitle(R.string.forced_update_dialog_title)
                                .setMessage(appUpdDescription);
                        builder.setPositiveButton(getString(R.string.forced_update_dialog_button), (dialog1, which) -> {
                            final String appPackageName = getPackageName();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        });

                        runOnUiThread(() -> builder.create().show());

                        return Observable.empty();
                    }
                    return Observable.just(deviceSettings);
                })
                .flatMap(deviceSettings -> Observable.just(!onboardingShown && deviceSettings.isOnboardingUpdated())
                        .flatMap(showOnboarding -> {
                            if (showOnboarding) {
                                return grainCorpRepository.getOnBoardingItems(grainCorpRepository.getDeviceId().uuid.toString())
                                        .flatMap(onboardingItems -> {
                                            onboardingShown = true;
                                            if (onboardingItems.size() > 0) {
                                                Intent intent = new Intent(BaseActivity.this, OnboardingActivity.class);
                                                intent.putParcelableArrayListExtra(OnboardingActivity.ARG_ITEMS, new ArrayList<>(onboardingItems));
                                                startActivityForResult(intent, ACTIVITY_FINISHED);
                                            }
                                            return Observable.empty();
                                        });
                            }
                            return grainCorpRepository.getDeviceIdObservable();
                        }))
                .flatMap(deviceId -> {
                    String deviceId2 = deviceId.uuid.toString();
                    UserInfo userInfo = grainCorpRepository.getUserInfofromDB();
                    return Observable.zip(
                            grainCorpRepository.getMenu(deviceId2, userInfo.getAccountGroup(), userInfo.getRelevantAccountNumber()),
                            grainCorpRepository.getPushNotificationSettings(deviceId2, grainCorpRepository.getEmail()),
                            grainCorpRepository.getGlobalSettingsAsObservable(),
                            (menuParentGroups, pns, globalSettings) -> globalSettings);
                })
                .compose(applySchedulers(true))
                .doOnSubscribe(disposable -> toggleSplashScreen(true))
                .doFinally(() -> toggleSplashScreen(false))
                .subscribe(globalSettings -> {
                    firstLaunch = false;
                    long inactivityThreshold = globalSettings.getInactiveAgeInSeconds() * 1000;
                    long backgroundTime = grainCorpRepository.getLastActiveTime();
                    if (backgroundTime != 0 && (SystemClock.elapsedRealtime() - backgroundTime) >= inactivityThreshold) {
                        globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_LOGOUT));
                    } else {
                        Intent intent = getIntent();
                        processNotificationIntentExtras(intent);
                    }
                }, err -> {
                    if (err instanceof SessionExpiredException) {
                        globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_LOGOUT));
                    } else if (err instanceof MaintenanceModeException) {
                        globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_SHOW));
                    } else if(err instanceof HttpException && err.getMessage().contains("400")) {
                        globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_DELETE));
                    }else{
                        showDialog(true);
                    }
                    // This needs to be reset, otherwise, if the user logs in after a session timeout, they will be automatically logged out again.
                    grainCorpRepository.resetLastActiveTime();
                }, () -> {
                    // This needs to be reset, otherwise, if the user logs in after a session timeout, they will be automatically logged out again.
                    grainCorpRepository.resetLastActiveTime();
                }));
    }

    public void toggleSplashScreen(boolean show) {
        if (show && !firstLaunch && resumedFromBackground) {
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.splash_screens));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addContentView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            getWindow().setStatusBarColor(getResources().getColor(R.color.splashScreenStatusBarColor, this.getTheme()));
        } else {
            ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
            rootView.removeView(imageView);
            getWindow().setStatusBarColor(getResources().getColor(R.color.dusk_blue, this.getTheme()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resumedFromBackground = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(screenOnOffReceiver);
        disposables.clear();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // App is backgrounded
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            grainCorpRepository.setLastActiveTime(SystemClock.elapsedRealtime());
        }
    }

    private void showDialog(boolean showSplash) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                analyticsProvider.trackOfflineEvent();
                globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_OFFLINE_DIALOG_RETRIED));
                if (showSplash) restrictUser();
                dialog.dismiss();
            });
        }
    }

    private void showMaintenanceDialog(boolean showSplash) {
        if (maintenanceDialog != null && !maintenanceDialog.isShowing()) {
            maintenanceDialog.show();
            maintenanceDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_DISMISS));
                if (showSplash) restrictUser();
                maintenanceDialog.dismiss();
            });
        }
    }

    private void maintenanceAlert() {
        maintenanceBuilder = new AlertDialog.Builder(this);
        maintenanceBuilder.setCancelable(false)
                .setTitle(getString(R.string.maint_alert_title))
                .setMessage(getString(R.string.maint_alert_message));
        maintenanceBuilder.setPositiveButton(getString(R.string.offline_dialog_positive_button), (dialog1, which) -> {
            restrictUser();
            maintenanceDialog.dismiss();
        });
        maintenanceDialog = maintenanceBuilder.create();
    }

    public abstract void processNotificationIntentExtras(Intent intent);

}
