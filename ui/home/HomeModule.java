package com.webling.graincorp.ui.home;


import android.content.Context;

import com.webling.graincorp.ui.home.adapter.HomeAdapter;

import java.util.Collections;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    private HomeContract.View view;
    private Context context;

    public HomeModule(HomeContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Provides
    HomeContract.View providesView() {
        return view;
    }

    @Provides
    HomeContract.Presenter providesPresenter(HomePresenter presenter) {
        return presenter;
    }

    @Provides
    HomeAdapter providesHomeAdapter() {
        return new HomeAdapter(Collections.emptyList(), context);
    }
}