package com.webling.graincorp.rxbus.event;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({EventTypeDef.TYPE_FIREBASE_MESSAGE_RECEIVED,
        EventTypeDef.TYPE_REMOVE_BADGE_COUNT,
        EventTypeDef.TYPE_LOGOUT,
        EventTypeDef.TYPE_LAUNCH_LOGIN_ACTIVITY,
        EventTypeDef.SEARCH_FILTERS_SAVED,
        EventTypeDef.TYPE_SHOW_OFFLINE_DIALOG,
        EventTypeDef.TYPE_OFFLINE_DIALOG_RETRIED,
        EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_SHOW,
        EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_DISMISS,
        EventTypeDef.TYPE_DELETE})
@Retention(RetentionPolicy.SOURCE)
public @interface EventTypeDef {
    int TYPE_FIREBASE_MESSAGE_RECEIVED = 0;
    int TYPE_REMOVE_BADGE_COUNT = 1;
    int TYPE_LOGOUT = 2;
    int TYPE_LAUNCH_LOGIN_ACTIVITY = 3;
    int SEARCH_FILTERS_SAVED = 4;
    int TYPE_SHOW_OFFLINE_DIALOG = 5;
    int TYPE_OFFLINE_DIALOG_RETRIED = 6;
    int TYPE_MAINTENANCE_MODE_DIALOG_SHOW = 7;
    int TYPE_MAINTENANCE_MODE_DIALOG_DISMISS = 8;
    int TYPE_DELETE = 9;

}
