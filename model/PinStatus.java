package com.webling.graincorp.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({PinStatus.ACTIVE, PinStatus.INACTIVE, PinStatus.UNKNOWN})
public @interface PinStatus {
    String ACTIVE = "Y";
    String INACTIVE = "N";
    String UNKNOWN = "";
}
