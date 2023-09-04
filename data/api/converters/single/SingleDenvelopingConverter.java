package com.webling.graincorp.data.api.converters.single;

import androidx.annotation.NonNull;

import com.webling.graincorp.data.api.model.response.SingleEnvelope;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class SingleDenvelopingConverter<T> implements Converter<ResponseBody, T> {
    final Converter<ResponseBody, SingleEnvelope<T>> delegate;

    public SingleDenvelopingConverter(Converter<ResponseBody, SingleEnvelope<T>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T convert(@NonNull ResponseBody responseBody) throws IOException {
        SingleEnvelope<T> envelope = delegate.convert(responseBody);
        return envelope.getObject();
    }
}