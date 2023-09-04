package com.webling.graincorp.rxbus;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;
import com.webling.graincorp.rxbus.event.Event;

public class BehaviourRxBus<T extends Event> extends BaseRxBus<T> {
    @Override
    public Relay<T> getBus() {
        return BehaviorRelay.<T>create().toSerialized();
    }
}
