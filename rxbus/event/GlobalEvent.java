package com.webling.graincorp.rxbus.event;

import androidx.annotation.Nullable;

public class GlobalEvent extends Event {
    public GlobalEvent(@EventTypeDef int eventType) {
        super(eventType);
    }

    public GlobalEvent(@Nullable Object payload, @EventTypeDef int eventType) {
        super(payload, eventType);
    }
}
