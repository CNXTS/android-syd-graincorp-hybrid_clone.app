package com.webling.graincorp.rxbus.event;

public class FirebaseMessagePayload {
    private int pnCount;
    private boolean notificationsEnabled;


    public FirebaseMessagePayload(int pnCount, boolean notificationsEnabled) {
        this.pnCount = pnCount;
        this.notificationsEnabled = notificationsEnabled;
    }

    public int getPnCount() {
        return pnCount;
    }

    public void setPnCount(int pnCount) {
        this.pnCount = pnCount;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
