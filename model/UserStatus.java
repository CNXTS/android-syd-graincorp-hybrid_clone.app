package com.webling.graincorp.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Identifies the current user status within the app
 * {@link UserStatus#UNDEFINED} is used to compare with, when the user status is not set or is in an error state
 * {@link UserStatus#ANONYMOUS} means the user is a new user and has never logged into the app before.
 * {@link UserStatus#SEMI_KNOWN} means the user has logged in once before and we have some details, mainly email, about the user
 * {@link UserStatus#KNOWN} means the user is currently a logged in user and we the email and the cookies stored for an active session
 */
@Retention(value = RetentionPolicy.SOURCE)
@IntDef({UserStatus.UNDEFINED, UserStatus.ANONYMOUS, UserStatus.SEMI_KNOWN, UserStatus.KNOWN})
public @interface UserStatus {
    int UNDEFINED = -1;
    int ANONYMOUS = 0;
    int SEMI_KNOWN = 1;
    int KNOWN = 2;
}
