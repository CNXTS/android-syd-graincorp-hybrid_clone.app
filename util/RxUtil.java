package com.webling.graincorp.util;


import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return applySchedulers(false);
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(boolean delayError) {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), delayError);
    }

    public static <T> FlowableTransformer<T, T> applyFlowableSchedulers() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
