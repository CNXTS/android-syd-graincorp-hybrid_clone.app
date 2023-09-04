package com.webling.graincorp.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.webling.graincorp.rxbus.event.Event;

public class RxBus<T extends Event> extends BaseRxBus<T> {
    @Override
    public Relay<T> getBus() {
        return PublishRelay.<T>create().toSerialized();
    }
}
