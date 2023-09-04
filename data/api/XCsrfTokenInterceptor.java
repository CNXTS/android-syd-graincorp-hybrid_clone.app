package com.webling.graincorp.data.api;

import android.net.TrafficStats;
import android.text.TextUtils;

import com.webling.graincorp.data.source.local.LocalDataSource;
import com.webling.graincorp.model.CSRFToken;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.webling.graincorp.constants.ApiConstants.X_CSRF_TOKEN_HEADER;

/**
 * An interceptor that allows to fetch and save a x-csrf-token
 *
 * @author Artaza Aziz
 */
public final class XCsrfTokenInterceptor implements Interceptor {
    private volatile LocalDataSource dataSource;

    public XCsrfTokenInterceptor(LocalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        TrafficStats.setThreadStatsTag(1000);
        Request request = chain.request();
        String requestCsrfTokenHeader = request.headers().get(X_CSRF_TOKEN_HEADER);
        Response response = chain.proceed(request);

        if (!TextUtils.isEmpty(requestCsrfTokenHeader)) {
            if (requestCsrfTokenHeader.equals("fetch")) {
                String csrfToken = response.header(X_CSRF_TOKEN_HEADER);
                dataSource.setCsrfToken(new CSRFToken(csrfToken));
            }
        }

        return response;
    }
}