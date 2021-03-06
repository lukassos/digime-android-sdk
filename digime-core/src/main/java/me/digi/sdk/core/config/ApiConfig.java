/*
 * Copyright (c) 2009-2018 digi.me Limited. All rights reserved.
 */

package me.digi.sdk.core.config;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.text.Normalizer;

import me.digi.sdk.core.BuildConfig;
import okhttp3.HttpUrl;

public final class ApiConfig {

    private static volatile ApiConfig singleton;

    private final HttpUrl url;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public ApiConfig(@NonNull HttpUrl url) {
        this.url = url;
    }

    public static ApiConfig get() {
        if (singleton == null) {
            synchronized (ApiConfig.class) {
                singleton = new ApiConfig(HttpUrl.parse("https://" + BuildConfig.BASE_HOST));
            }
        }
        return singleton;
    }

    public String getUrl() {
        return url.toString();
    }

    public String getHost() {
        return url.host();
    }

    public String userAgentString(String appName, String versionCode) {
        String ua = appName +
                '/' + versionCode +
                ' ' +
                Build.MODEL + '/' + Build.VERSION.RELEASE +
                " (" +
                Build.MANUFACTURER + ';' +
                Build.MODEL + ';' +
                Build.BRAND + ';' +
                Build.PRODUCT +
                ')';
        return fromUtf(Normalizer.normalize(ua, Normalizer.Form.NFD));
    }

    public Uri.Builder buildUrl(String... paths) {
        final Uri.Builder builder = Uri.parse(getUrl()).buildUpon();
        if (paths != null) {
            for (String p : paths) {
                builder.appendPath(p);
            }
        }
        return builder;
    }

    private static String fromUtf(String str) {
        final StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c > '\u001f' && c < '\u007f') {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
