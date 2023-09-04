package com.webling.graincorp.data.api.model.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import retrofit2.converter.gson.GsonConverterFactory;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SingleResultsResponse {
    Class<?> converter() default GsonConverterFactory.class; //Ignored anyways
}
