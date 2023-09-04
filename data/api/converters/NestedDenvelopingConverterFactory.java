package com.webling.graincorp.data.api.converters;

import com.webling.graincorp.data.api.converters.nested.MenuDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.NotificationSettingsDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.OnboardingDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.PushNotificationDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.UserDetailsDenvelopingConverter;
import com.webling.graincorp.data.api.converters.nested.UserAccountDenvelopingConverter;
import com.webling.graincorp.data.api.model.response.NestedResultsResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NestedDenvelopingConverterFactory extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public NestedDenvelopingConverterFactory(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    //TODO this NEEDS to be refactoredß
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type,
            Annotation[] annotations,
            Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == NestedResultsResponse.class) {
                Class<? extends Converter.Factory> converterClass = ((NestedResultsResponse) annotation).converter();
                if (converterClass == NotificationSettingsDenvelopingConverter.class) {
                    return new NotificationSettingsDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                } else if (converterClass == MenuDenvelopingConverter.class) {
                    return new MenuDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                } else if (converterClass == PushNotificationDenvelopingConverter.class) {
                    return new PushNotificationDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                } else if (converterClass == OnboardingDenvelopingConverter.class) {
                    return new OnboardingDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                } else if (converterClass == UserDetailsDenvelopingConverter.class) {
                    return new UserDetailsDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                } else if (converterClass == UserAccountDenvelopingConverter.class) {
                    return new UserAccountDenvelopingConverter(gsonConverterFactory).responseBodyConverter(type, annotations, retrofit);
                }
            }
        }
        return null;
    }
}