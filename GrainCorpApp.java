package com.webling.graincorp;

import android.app.Application;
import android.os.StrictMode;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

public class GrainCorpApp extends Application {

    private GrainCorpAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCenter.start(this, getString(R.string.appcenter_api_key), Analytics.class, Crashes.class);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
        }
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(GrainCorpDatabase.class)
                        .databaseName(GrainCorpDatabase.NAME)
                        .build())
                .build());
        component = DaggerGrainCorpAppComponent
                .builder()
                .grainCorpAppModule(new GrainCorpAppModule(this))
                .build();

        component.graincorpRepository().clearSessionCookie();

    }

    public GrainCorpAppComponent getComponent() {
        return component;
    }
}
