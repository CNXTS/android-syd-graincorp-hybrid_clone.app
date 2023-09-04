package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.user.UserDetails;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetailsDenvelopingConverter extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public UserDetailsDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, UserDetails> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, UserDetails.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<UserDetails> envelope = (NestedEnvelope<UserDetails>) gsonResponseBodyConverter.convert(value);
            List<UserDetails> results = envelope.getObject().getResults();
            return results != null && results.size() > 0 ? results.get(0) : new UserDetails();

        };
    }
}

