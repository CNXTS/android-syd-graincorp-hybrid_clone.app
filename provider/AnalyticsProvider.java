package com.webling.graincorp.provider;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface AnalyticsProvider {

    /**
     * Tracks a specific app event
     *
     * @param eventName The name of the event to track
     */
    void trackEvent(@NonNull String eventName);

    /**
     * Tracks a specific app event
     *
     * @param eventName The name of the event to track
     * @param bundle An optional map of parameters associated with the event
     */
    void trackEvent(@NonNull String eventName, @Nullable Bundle bundle);

    /**
     * Tracks the user clicking the retry button when offline pop up appears
     */
    void trackOfflineEvent();

    /**
     * Set the current screen for view tracking
     *
     * @param activity The activity to which the screen name applies
     * @param screenName The name of the screen
     */
    void setCurrentScreen(Activity activity, String screenName);
}
