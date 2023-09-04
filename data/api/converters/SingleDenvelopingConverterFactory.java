package com.webling.graincorp.data.api.converters;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.converters.single.BidDenvelopingConverter;
import com.webling.graincorp.data.api.converters.single.SingleDenvelopingConverter;
import com.webling.graincorp.data.api.model.response.SingleEnvelope;
import com.webling.graincorp.data.api.model.response.SingleResultsResponse;
import com.webling.graincorp.model.Bid;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SingleDenvelopingConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type,
            Annotation[] annotations,
            Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == SingleResultsResponse.class) {
                Class<?> converterClass = ((SingleResultsResponse) annotation).converter();
                if (converterClass.isAssignableFrom(BidDenvelopingConverter.class)) {
                    Type envelopeType = TypeToken.getParameterized(SingleEnvelope.class, Bid.class).getType();
                    Converter<ResponseBody, SingleEnvelope<Bid>> delegate =
                            retrofit.nextResponseBodyConverter(this, envelopeType, annotations);
                    //noinspection unchecked
                    return new BidDenvelopingConverter(delegate);
                } else {
                    Type envelopeType = TypeToken.getParameterized(SingleEnvelope.class, type).getType();
                    Converter<ResponseBody, SingleEnvelope> delegate =
                            retrofit.nextResponseBodyConverter(this, envelopeType, annotations);
                    //noinspection unchecked
                    return new SingleDenvelopingConverter(delegate);
                }
            }
        }
        return null;
    }
}