package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.user.UserAccount;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAccountDenvelopingConverter extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public UserAccountDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, UserAccount> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, UserAccount.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<UserAccount> envelope = (NestedEnvelope<UserAccount>) gsonResponseBodyConverter.convert(value);
            List<UserAccount> results = envelope.getObject().getResults();
            return results != null && results.size() > 0 ? results.get(0) : new UserAccount();

        };
    }
}

