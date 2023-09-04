package com.webling.graincorp.util;

import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Adapted from
 * https://gist.github.com/justinthomas-syncbak/cd29feebd6837d5b45f6576c73faedac
 * Provides a synchronization point between the webview cookie store and okhttp3.OkHttpClient cookie store
 */
public final class WebViewCookieJar implements CookieJar {

    private CookieManager cookieManager;
    private Set<String> cookieUrls;
    private CookieSyncManager cookieSyncManager;

    public WebViewCookieJar(CookieManager cookieManager, CookieSyncManager cookieSyncManager) {
        cookieUrls = new HashSet<>();
        this.cookieManager = cookieManager;
        this.cookieSyncManager = cookieSyncManager;
    }

    public synchronized void saveFromResponse(@NonNull HttpUrl url, @NonNull String cookieAsString) {
        saveFromResponse(url, splitStringtoCookieList(url, cookieAsString));
    }

    @Override
    public synchronized void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        String urlString = url.toString();
        cookieUrls.add(urlString);
        for (Cookie cookie : cookies) {
            cookieManager.setCookie(urlString, cookie.toString());
        }
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        String urlString = url.toString();

        //required to stop crashes pre api 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else {
            cookieSyncManager.sync();
        }
        String cookiesString = cookieManager.getCookie(urlString);

        return splitStringtoCookieList(url, cookiesString);

    }

    public String getCookieString(String url) {
        return cookieManager.getCookie(url);
    }

    public synchronized void clearSessionCookie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieSyncManager.startSync();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
        cookieUrls.clear();
    }

    public synchronized boolean hasSessionCookie(String sessionCookieName) throws NullPointerException {
        if (TextUtils.isEmpty(sessionCookieName)) return false;

        for (String url : cookieUrls) {
            String cookiesString = cookieManager.getCookie(url);
            if (cookiesString != null && !cookiesString.isEmpty()) {
                String[] cookieHeaders = cookiesString.split(";");
                for (String header : cookieHeaders) {
                    HttpUrl httpUrl = HttpUrl.parse(url);
                    if (httpUrl != null) {
                        Cookie cookie = Cookie.parse(httpUrl, header);
                        if (cookie != null && cookie.name().equals(sessionCookieName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<Cookie> splitStringtoCookieList(@NonNull HttpUrl url, String cookiesString) {
        if (cookiesString != null && !cookiesString.isEmpty()) {
            //We can split on the ';' char as the cookie manager only returns cookies
            //that match the url and haven't expired, so the cookie attributes aren't included
            String[] cookieHeaders = cookiesString.split(";");
            List<Cookie> cookies = new ArrayList<>(cookieHeaders.length);
            for (String header : cookieHeaders) {
                cookies.add(Cookie.parse(url, header));
            }
            return cookies;
        }
        return Collections.emptyList();
    }
}