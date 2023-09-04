package com.webling.graincorp.model;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Artaza Aziz
 */
@Singleton
public class CSRFToken {
    public String csrfToken;

    @Inject
    public CSRFToken() {

    }

    public CSRFToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }
}
