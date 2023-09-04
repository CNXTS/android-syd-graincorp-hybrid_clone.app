package com.webling.graincorp.provider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.webling.graincorp.constants.Analytics;

public class AnalyticsProviderImpl implements AnalyticsProvider {

    private FirebaseAnalytics analytics;

    public AnalyticsProviderImpl(FirebaseAnalytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public void trackEvent(@NonNull String eventName) {
        analytics.logEvent(eventName, null);
    }

    @Override
    public void trackEvent(@NonNull String eventName, @Nullable Bundle bundle) {
        analytics.logEvent(eventName, bundle);
    }

    @Override
    public void trackOfflineEvent() {
        analytics.logEvent(Analytics.CustomEvents.OFFLINE_TRY_AGAIN_TAPPED, null);
    }

    @Override
    public void setCurrentScreen(Activity activity, String screenName) {
        analytics.setCurrentScreen(activity, screenName, null);
    }
}
