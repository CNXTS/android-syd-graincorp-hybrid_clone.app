package com.webling.graincorp.data.api.model.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import retrofit2.Converter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NestedResultsResponse {
    Class<? extends Converter.Factory> converter();
}
