package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.onboarding.Child;
import com.webling.graincorp.model.OnboardingItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnboardingDenvelopingConverter extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public OnboardingDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, List<OnboardingItem>> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, Child.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<Child> envelope = (NestedEnvelope<Child>) gsonResponseBodyConverter.convert(value);

            List<OnboardingItem> onboardingItems = new ArrayList<>();

            List<Child> results = envelope.getObject().getResults();
            for (Child result : results) {
                OnboardingItem onboardingItem = new OnboardingItem();
                onboardingItem.setImageUrl(result.getImageUrl());
                onboardingItem.setOnboardPage(result.getOnboardPage());
                onboardingItem.setOnBoardText(result.getOnBoardText());
                onboardingItem.setOnboardTitle(result.getOnboardTitle());
                onboardingItems.add(onboardingItem);
            }
            return onboardingItems;
        };
    }
}

