package com.webling.graincorp.rxbus.event;

import androidx.annotation.Nullable;

public abstract class Event {
    @Nullable
    Object payload;
    @EventTypeDef
    private int eventType;

    public Event(@EventTypeDef int eventType) {
        this.eventType = eventType;
    }

    public Event(@Nullable Object payload, @EventTypeDef int eventType) {
        this.payload = payload;
        this.eventType = eventType;
    }

    @Nullable
    public Object getPayload() {
        return payload;
    }

    public @EventTypeDef int getEventType() {
        return eventType;
    }

    public void setPayload(@Nullable Object payload) {
        this.payload = payload;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
