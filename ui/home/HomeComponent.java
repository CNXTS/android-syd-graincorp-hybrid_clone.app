package com.webling.graincorp.ui.home;

import com.webling.graincorp.GrainCorpAppComponent;
import com.webling.graincorp.util.scopes.GraincorpScope;

import dagger.Component;

@GraincorpScope
@Component(dependencies = GrainCorpAppComponent.class, modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomeFragment fragment);
}
