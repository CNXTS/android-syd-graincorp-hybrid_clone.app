package com.webling.graincorp.data.api;

import android.net.TrafficStats;
import android.text.TextUtils;

import com.webling.graincorp.data.source.local.LocalDataSource;
import com.webling.graincorp.model.GlobalSettings;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * An interceptor that allows runtime changes to the URL hostname.
 * Adapted From: https://gist.github.com/swankjesse/8571a8207a5815cca1fb
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile LocalDataSource dataSource;

    /**
     * Set a {@link LocalDataSource} to access {@link GlobalSettings#ccDomain} dynamically
     */
    public void setDataSource(LocalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        TrafficStats.setThreadStatsTag(1001);
        Request request = chain.request();
        GlobalSettings globalSettings = dataSource.getGlobalSettings();
        String host = globalSettings != null ? globalSettings.getCcDomain() : null;
        if (!TextUtils.isEmpty(host)) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}