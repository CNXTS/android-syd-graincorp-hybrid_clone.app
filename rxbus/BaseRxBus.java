package com.webling.graincorp.rxbus;

import com.jakewharton.rxrelay2.Relay;
import com.webling.graincorp.rxbus.event.Event;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

//TODO Consider if Events or EventTypes need to be split/bound to bus types?
public abstract class BaseRxBus<U extends Event> {
    private Relay<U> bus;

    BaseRxBus() {
        bus = getBus();
    }

    public abstract Relay<U> getBus();

    public final Observable<U> register(int eventType) {
        return bus
                .filter(event -> event.getEventType() == eventType);
    }

    public final Flowable<U> registerAsFlowable(int eventType) {
        return asFlowable()
                .filter(event -> event.getEventType() == eventType);
    }

    public void send(U event) {
        if (hasObservers()) bus.accept(event);
    }

    private Flowable<U> asFlowable() {
        return bus.toFlowable(BackpressureStrategy.LATEST);
    }

    private boolean hasObservers() {
        return bus.hasObservers();
    }
}