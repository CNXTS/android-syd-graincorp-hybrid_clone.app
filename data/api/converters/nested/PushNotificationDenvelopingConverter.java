package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.pushnotification.Notification;
import com.webling.graincorp.model.PushNotification;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PushNotificationDenvelopingConverter extends Converter.Factory {

    private final String TAG = getClass().getSimpleName();
    private GsonConverterFactory gsonConverterFactory;
    private DateTimeZone dateTimeZone;

    public PushNotificationDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
        dateTimeZone = DateTimeZone.getDefault();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, Notification.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<Notification> envelope = (NestedEnvelope<Notification>) gsonResponseBodyConverter.convert(value);

            List<PushNotification> pushNotifications = new ArrayList<>();

            List<Notification> notificationsResponse = envelope.getObject().getResults();
            for (Notification notification : notificationsResponse) {
                try {
                    PushNotification pushNotification = new PushNotification();
                    if (notification.getTransactionTimeStamp().isEmpty()) {
                        continue;
                    }
                    //20170816 141142 -> date object
                    String transactionTimeStamp = notification.getTransactionTimeStamp();
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYYMMdd HHmmss");
                    DateTime dateTime = dtf.parseDateTime(transactionTimeStamp);
                    pushNotification.setTransactionTimeStamp(dateTime.toDate());

                    pushNotification.setTitle(notification.getTitle());
                    pushNotification.setBody(notification.getBody());
                    pushNotification.setActionUrl(notification.getActionUrl());
                    pushNotification.setPnId(notification.getPnId());
                    pushNotification.setUserDeviceId(notification.getUserDeviceId());
                    pushNotification.setTimeZoneText(notification.getTimeZoneText());

                    pushNotifications.add(pushNotification);
                } catch (UnsupportedOperationException | IllegalArgumentException e) {
                    Log.e(TAG, "Exception", e);
                }
            }
            return pushNotifications;
        };
    }
}

